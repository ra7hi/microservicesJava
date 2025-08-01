package microservices.order_processing.order_service.exception;

/**
 * Исключение, выбрасываемое при отсутствии пользователя в базе.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
