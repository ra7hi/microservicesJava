package microservices.order_processing.notification_service.services;

import microservices.order_processing.notification_service.kafka.KafkaProducerService;
import microservices.order_processing.notification_service.saga.SagaEvent;

import microservices.order_processing.notification_service.utils.TestUtils;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaProducerService = new KafkaProducerService(kafkaTemplate);
        TestUtils.setField(kafkaProducerService, "sagaEventsTopic", "saga-events");
    }

    @Test
    void sendSagaEvent_shouldSendSuccessfully() {
        SagaEvent sagaEvent = new SagaEvent("saga-123", "order.created", null, null, null);
        SendResult<String, Object> sendResult = new SendResult<>(null, new RecordMetadata(null, 0, 0, 0L, 0L, 0, 0));
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send("saga-events", "saga-123", sagaEvent)).thenReturn(future);

        kafkaProducerService.sendSagaEvent(sagaEvent);

        verify(kafkaTemplate).send("saga-events", "saga-123", sagaEvent);
    }

    @Test
    void sendSagaEvent_shouldHandleSendFailure() {
        SagaEvent sagaEvent = new SagaEvent("saga-456", "order.created", null, null, null);
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka send failure"));

        when(kafkaTemplate.send("saga-events", "saga-456", sagaEvent)).thenReturn(future);

        kafkaProducerService.sendSagaEvent(sagaEvent);

        verify(kafkaTemplate).send("saga-events", "saga-456", sagaEvent);
    }
}
