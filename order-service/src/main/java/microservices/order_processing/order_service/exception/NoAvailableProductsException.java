package microservices.order_processing.order_service.exception;

/**
 * Исключение, выбрасываемое при отсутствии доступных товаров.
 */
public class NoAvailableProductsException extends RuntimeException {
    public NoAvailableProductsException(String message) {
        super(message);
    }
}
