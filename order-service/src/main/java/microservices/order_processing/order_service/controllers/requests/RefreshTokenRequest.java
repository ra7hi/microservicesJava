package microservices.order_processing.order_service.controllers.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO-запрос для обновления токена пользователя.
 * Используется клиентом, чтобы получить новый access-токен,
 */
@Getter
@Setter
public class RefreshTokenRequest {
    private String refreshToken;
}
