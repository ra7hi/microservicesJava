package microservices.order_processing.order_service.enums;
/**
 * Перечисление, описывающее возможные статусы выполнения саги в распределённой транзакции.
 * Используется для отслеживания прогресса и управления откатом в случае неудачи.
 * <p>Этапы отражают как успешное выполнение, так и компенсирующие действия при ошибках.</p>
 * <ul>
 *     <li>{@link #STARTED} — процесс саги инициирован, начальное состояние.</li>
 *     <li>{@link #INVENTORY_RESERVED} — ресурсы (товары) успешно зарезервированы.</li>
 *     <li>{@link #ORDER_CREATED} — заказ успешно создан.</li>
 *     <li>{@link #COMPLETED} — сага успешно завершена, все шаги выполнены.</li>
 *     <li>{@link #FAILED} — ошибка на одном из этапов, требуется откат (compensation).</li>
 *     <li>{@link #COMPENSATING} — выполняются компенсирующие действия (откат шагов).</li>
 *     <li>{@link #COMPENSATED} — компенсирующие действия завершены, сага откатилась безопасно.</li>
 * </ul>
 */
public enum SagaStatus {
    STARTED,
    INVENTORY_RESERVED,
    ORDER_CREATED,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED
}
