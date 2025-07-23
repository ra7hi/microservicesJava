package microservices.order_processing.notification_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.order_processing.notification_service.saga.SagaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    @Value("${kafka.topics.saga-events}")
    private String sagaEventsTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendSagaEvent(SagaEvent sagaEvent) {
        log.info("Sending saga event from notification service: {}", sagaEvent);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(sagaEventsTopic, sagaEvent.getSagaId(), sagaEvent);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Saga event sent successfully from notification service: {}", sagaEvent.getSagaId());
            } else {
                log.error("Failed to send saga event from notification service: {}, error: {}",
                        sagaEvent.getSagaId(), ex.getMessage());
            }
        });
    }
}
