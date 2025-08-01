package microservices.order_processing.order_service.exception;

import microservices.order_processing.order_service.controllers.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * Перехватывает распространённые исключения и возвращает структурированный JSON-ответ клиенту.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка ошибок валидации входных данных (@Valid).
     *
     * @param ex исключение валидации аргументов
     * @return карта полей с ошибками и сообщениями об ошибках
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        errors.put("success", "false");

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Обработка ошибок аутентификации: неправильный логин или пароль.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid username or password"));
    }

    /**
     * Обработка исключения некорректного токена.
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    /**
     * Обработка ошибок регистрации (например, пользователь уже существует).
     */
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ApiResponse> handleRegistrationException(RegistrationException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(false, ex.getMessage()));
    }

    /**
     * Обработка ошибки, если пользователь не найден.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, ex.getMessage()));
    }

    /**
     * Обработка ситуации, когда нет доступных товаров.
     */
    @ExceptionHandler(NoAvailableProductsException.class)
    public ResponseEntity<?> handleNoAvailableProducts(NoAvailableProductsException ex) {
        return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
    }
}
