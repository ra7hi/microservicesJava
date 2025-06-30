package microservices.order_processing.order_service.controllers.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Username can't be null!")
    private String username;

    @NotBlank(message = "Password can't be null!")
    private String password;
}
