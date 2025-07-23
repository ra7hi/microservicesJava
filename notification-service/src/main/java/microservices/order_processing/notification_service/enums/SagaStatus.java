package microservices.order_processing.notification_service.enums;

public enum SagaStatus {
    STARTED,
    INVENTORY_RESERVED,
    ORDER_CREATED,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED
}
