package microservices.order_processing.inventory_service.enums;

/**
 * Перечисление, описывающее статус резервирования товара для заказа.
 * <ul>
 *     <li>{@link #RESERVED} — товар зарезервирован, но ещё не подтверждён.</li>
 *     <li>{@link #CONFIRMED} — резерв подтверждён и товар закреплён за заказом.</li>
 *     <li>{@link #RELEASED} — резерв был снят (например, из-за отмены заказа или внутренней ошибки).</li>
 * </ul>
 */
public enum ReservationStatus {
    RESERVED,
    CONFIRMED,
    RELEASED
}
