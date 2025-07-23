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

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final CustomUserDetailsServiceImp customUserDetailsServiceImp;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

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
