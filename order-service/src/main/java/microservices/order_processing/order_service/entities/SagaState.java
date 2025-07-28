package microservices.order_processing.order_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservices.order_processing.order_service.enums.SagaStatus;

import java.time.LocalDateTime;

/**
 * Сущность состояния саги в процессе обработки заказа.
 * Содержит информацию о текущем статусе саги, связанных заказе и пользователе,
 * а также временные метки создания и последнего обновления.
 */
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

    /**
     * Текущий статус саги.
     * Хранится в виде строки, соответствующей перечислению {@link SagaStatus}.
     */
    @Enumerated(EnumType.STRING)
    private SagaStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
