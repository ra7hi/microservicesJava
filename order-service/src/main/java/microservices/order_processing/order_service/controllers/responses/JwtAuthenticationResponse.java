package microservices.order_processing.order_service.controllers.responses;

import lombok.Getter;
import lombok.Setter;
import microservices.order_processing.order_service.entities.Users;

@Getter
@Setter
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Users user;

    public JwtAuthenticationResponse(String accessToken, Users user) {
        this.accessToken = accessToken;
        this.user = user;
    }
}
