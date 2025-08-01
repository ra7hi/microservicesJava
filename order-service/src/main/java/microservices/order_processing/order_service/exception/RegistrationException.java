package microservices.order_processing.order_service.exception;

/**
 * Исключение, выбрасываемое при ошибке регистрации пользователя.
 */
public class RegistrationException extends RuntimeException {
    public RegistrationException(String message) {
        super(message);
    }
}
