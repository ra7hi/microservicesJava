package microservices.order_processing.order_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservices.order_processing.order_service.enums.SagaStatus;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "saga_states")
public class SagaState {
    @Id
    private String sagaId;
    private String orderId;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private SagaStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
