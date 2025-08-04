package microservices.order_processing.order_service.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import microservices.order_processing.order_service.services.CustomUserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Фильтр для аутентификации HTTP-запросов на основе JWT (JSON Web Token).
 * <p>
 * Этот фильтр выполняется один раз на каждый запрос и выполняет следующие задачи:
 * <ul>
 *     <li>Извлекает JWT из заголовка "Authorization".</li>
 *     <li>Проверяет валидность токена с помощью {@link JwtUtils}.</li>
 *     <li>Если токен валиден, загружает данные пользователя и устанавливает аутентификацию в {@link SecurityContextHolder}.</li>
 *     <li>Если токен невалиден, возвращает ответ с кодом 401 Unauthorized и прекращает дальнейшую обработку запроса.</li>
 * </ul>
 * <p>Фильтр автоматически применяется ко всем входящим запросам в цепочке Spring Security.</p>
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private CustomUserDetailsServiceImp customUserDetailsServiceImp;

    /**
     * Обрабатывает входящие HTTP-запросы, выполняя аутентификацию на основе JWT.
     *
     * @param request     входящий HTTP-запрос
     * @param response    HTTP-ответ
     * @param filterChain цепочка фильтров Spring Security для передачи управления дальше
     * @throws ServletException в случае ошибок сервлета
     * @throws IOException      в случае ошибок ввода-вывода
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
                                        throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/swagger-ui")
                || path.startsWith("/v3")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.equals("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> jwt = getJwtFromRequest(request);
        if (jwt.isPresent()) {
            String token = jwt.get();

            if (jwtUtils.validateToken(token)) {
                String username = jwtUtils.getUsernameFromToken(token);
                UserDetails userDetails = customUserDetailsServiceImp.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Invalid or expired token\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


    /**
     * Извлекает JWT из заголовка "Authorization" HTTP-запроса.
     *
     * @param request входящий HTTP-запрос
     * @return {@link Optional} с токеном, если он присутствует и корректен, иначе пустой
     */
    private Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }
}
