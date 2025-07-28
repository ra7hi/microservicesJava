package microservices.order_processing.order_service.repository;

import microservices.order_processing.order_service.entities.SagaState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaStateRepository extends JpaRepository<SagaState, String> {
    SagaState findBySagaId(String sagaId);
    SagaState findByOrderIdAndUserId(String orderId, Long userId);
}
