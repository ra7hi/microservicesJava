package microservices.order_processing.order_service.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Обработчик неавторизованных запросов (401 Unauthorized) в Spring Security.
 * Вызывается, когда аутентификация не удалась или пользователь не предоставил токен.
 * Отправляет JSON-ответ с сообщением об ошибке и статусом 401.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Обработка неавторизованного доступа к защищённому ресурсу.
     *
     * @param request        исходный HTTP-запрос
     * @param response       HTTP-ответ, в который будет записан статус и сообщение об ошибке
     * @param authException  исключение, содержащее информацию о причине отказа в доступе
     */

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\":false,\"message\":\"" + authException.getMessage() + "\"}");
    }
}
