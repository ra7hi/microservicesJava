package microservices.order_processing.order_service.exception;

/**
 * Исключение, выбрасываемое при недействительном токене авторизации.
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
