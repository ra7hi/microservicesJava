package microservices.order_processing.order_service.controllers.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO-запрос для регистрации нового пользователя.
 * <p>
 * Содержит имя пользователя, email и пароль.
 * Валидируется через Bean Validation.
 */
@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Username can't be null!")
    @Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters long!")
    private String username;

    @NotBlank(message = "Email can't be null!")
    @Size(max = 40, message = "The maximum email length is 40 characters!")
    @Email(message = "Invalid email format!")
    private String email;

    @NotBlank(message = "Password can't be null!")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters long!")
    private String password;
}
