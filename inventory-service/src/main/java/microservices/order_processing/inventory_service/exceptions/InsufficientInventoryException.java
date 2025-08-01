package microservices.order_processing.inventory_service.exceptions;

/**
 * Исключение, выбрасываемое при нехватке доступного количества товара для резервации.
 */
public class InsufficientInventoryException extends RuntimeException {
    public InsufficientInventoryException(String message) {
        super(message);
    }
}
