package microservices.order_processing.order_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import microservices.order_processing.order_service.enums.Role;

import java.util.Set;

@Getter
@Setter
public class UserDto {

    @NotBlank(message = "Username can't be null!")
    @Size(min = 3, max = 15, message = "username must be between 3 and 15 characters long!")
    private String username;

    @NotBlank(message = "Email can't be null!")
    @Size(max = 40, message = "The maximum email length is 40 characters!")
    @Email(message = "Invalid email format!")
    private String email;

    @NotBlank(message = "Password can't be null!")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters long!")
    private String password;

    private Set<Role> roles;
}
