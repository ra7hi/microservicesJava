package microservices.order_processing.order_service.enums;
/**
 * Перечисление, описывающее возможные статусы заказа.
 * <ul>
 *     <li>{@link #PENDING} — заказ ожидает подтверждения или доступности товаров.</li>
 *     <li>{@link #CREATED} — заказ успешно создан и подтверждён.</li>
 *     <li>{@link #FAILED} — создание заказа завершилось с ошибкой (например, из-за недоступности товаров).</li>
 * </ul>
 */
public enum OrderStatus {
    PENDING,
    CREATED,
    FAILED
}
