package microservices.order_processing.notification_service.saga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservices.order_processing.notification_service.enums.SagaStatus;

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