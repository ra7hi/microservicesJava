package microservices.order_processing.order_service.services;

import microservices.order_processing.order_service.controllers.requests.LoginRequest;
import microservices.order_processing.order_service.controllers.responses.JwtAuthenticationResponse;
import microservices.order_processing.order_service.controllers.responses.RefreshTokenResponse;

/**
 * Интерфейс для сервиса {@link AuthServiceImp}, содержащий 2 метода.
 */
public interface AuthService {
    /**
     * Аутентифицирует пользователя по данным из DTO-запроса логина
     */
    JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest);

    /**
     * Обновляет токен доступа на основе переданного нового токена.
     *
     * @param refreshToken строка с рефреш токеном
     * @return ответ с новым токеном доступа ({@link RefreshTokenResponse})
     */
    RefreshTokenResponse refreshAccessToken(String refreshToken);
}
