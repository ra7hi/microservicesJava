package microservices.order_processing.order_service.services;

import lombok.RequiredArgsConstructor;
import microservices.order_processing.order_service.controllers.requests.LoginRequest;
import microservices.order_processing.order_service.controllers.responses.JwtAuthenticationResponse;
import microservices.order_processing.order_service.controllers.responses.RefreshTokenResponse;
import microservices.order_processing.order_service.exception.InvalidTokenException;
import microservices.order_processing.order_service.impl.UserDetailsImpl;
import microservices.order_processing.order_service.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса аутентификации пользователей.
 * Отвечает за проверку учетных данных, генерацию JWT токенов и обновление токенов доступа.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final CustomUserDetailsServiceImp customUserDetailsServiceImp;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Аутентифицирует пользователя на основе логина и пароля.
     * Генерирует JWT токен и возвращает информацию о пользователе вместе с токеном.
     *
     * @param loginRequest DTO с именем пользователя и паролем ({@link LoginRequest})
     * @return объект с JWT токеном и данными пользователя ({@link JwtAuthenticationResponse})
     * @throws org.springframework.security.core.AuthenticationException в случае неудачной аутентификации
     */
    @Override
    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.buildToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new JwtAuthenticationResponse(jwt, userDetails.getUser());
    }

    /**
     * Обновляет JWT токен доступа, используя переданный refresh токен.
     * Проверяет валидность refresh токена, загружает пользователя и генерирует новый токен доступа.
     *
     * @param refreshToken строка с refresh токеном
     * @return объект с новым JWT токеном доступа ({@link RefreshTokenResponse})
     * @throws InvalidTokenException если токен недействителен или просрочен
     */

    @Override
    public RefreshTokenResponse refreshAccessToken(String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String username = jwtUtils.getUsernameFromToken(refreshToken);
        UserDetails userDetails = customUserDetailsServiceImp.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String newAccessToken = jwtUtils.buildToken(authentication);

        return new RefreshTokenResponse(newAccessToken);
    }
}
