package microservices.order_processing.order_service.exception;

public class NoAvailableProductsException extends RuntimeException {
    public NoAvailableProductsException(String message) {
        super(message);
    }
}
