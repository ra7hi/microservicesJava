package microservices.order_processing.order_service.controllers.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Запрос для аутентификации пользователя
 * Содержит имя пользователя и пароль, необходимые для входа
 * Валидируется с помощью аннотаций Bean Validation.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username can't be null!")
    private String username;

    @NotBlank(message = "Password can't be null!")
    private String password;
}
