package microservices.order_processing.order_service.controllers.responses;

import lombok.Getter;
import lombok.Setter;
import microservices.order_processing.order_service.entities.Users;

@Getter
@Setter
public class JwtAuthenticationResponse {
    private String accessToken;
    private Users user;
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(String accessToken, Users user) {
        this.accessToken = accessToken;
        this.user = user;
    }
}
