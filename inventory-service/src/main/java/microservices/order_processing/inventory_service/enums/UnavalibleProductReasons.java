package microservices.order_processing.inventory_service.enums;

/**
 * Перечисление, содержащее причины отсутствия продуктов (продукт не найден в системе,
 * нужное количество продукта не доступно)
 */
public enum UnavalibleProductReasons {
    NOT_FOUND, INSUFFICIENT_QUANTITY
}
