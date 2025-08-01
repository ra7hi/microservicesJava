package microservices.order_processing.inventory_service.saga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservices.order_processing.inventory_service.enums.SagaStatus;

/**
 * DTO-класс, представляющий событие саги.
 * Служит для передачи состояния и данных саги в распределенной системе.
 * Содержит уникальный идентификатор саги, тип события, статус, полезную нагрузку и сообщение об ошибке.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SagaEvent {
    private String sagaId;
    private String eventType;
    private SagaStatus status;
    private Object payload;
    private String errorMessage;
}
