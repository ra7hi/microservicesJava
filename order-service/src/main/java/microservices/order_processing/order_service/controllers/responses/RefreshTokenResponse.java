package microservices.order_processing.order_service.controllers.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public RefreshTokenResponse(String newAccessToken) {
        this.accessToken = newAccessToken;
    }
}
