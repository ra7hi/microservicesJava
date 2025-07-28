package microservices.order_processing.order_service.controllers.responses;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO-Ответ после успешного обновления токена пользователя.
 * Содержит токен и его тип
 */
@Getter
@Setter
public class RefreshTokenResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public RefreshTokenResponse(String newAccessToken) {
        this.accessToken = newAccessToken;
    }
}
