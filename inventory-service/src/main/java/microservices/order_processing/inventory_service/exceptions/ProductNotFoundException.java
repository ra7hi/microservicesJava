package microservices.order_processing.inventory_service.exceptions;

/**
 * Исключение, выбрасываемое при попытке получить несуществующий товар.
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}