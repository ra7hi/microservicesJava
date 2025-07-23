package microservices.order_processing.inventory_service.exceptions;

public class InsufficientInventoryException extends RuntimeException {
    public InsufficientInventoryException(String message) {
        super(message);
    }
}
