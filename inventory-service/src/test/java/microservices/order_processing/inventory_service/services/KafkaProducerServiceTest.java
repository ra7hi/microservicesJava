package microservices.order_processing.inventory_service.services;

import microservices.order_processing.inventory_service.kafka.KafkaProducerService;
import microservices.order_processing.inventory_service.saga.SagaEvent;
import microservices.order_processing.inventory_service.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Captor
    private ArgumentCaptor<String> topicCaptor;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<Object> valueCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaProducerService = new KafkaProducerService(kafkaTemplate);
        TestUtils.setField(kafkaProducerService, "sagaEventsTopic", "inventory-saga-topic");
    }

    @Test
    void sendSagaEventShouldSendEventSuccessfully() {
        SagaEvent sagaEvent = new SagaEvent();
        sagaEvent.setSagaId("inventory-saga-1");

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        kafkaProducerService.sendSagaEvent(sagaEvent);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());

        assert topicCaptor.getValue().equals("inventory-saga-topic");
        assert keyCaptor.getValue().equals("inventory-saga-1");
        assert valueCaptor.getValue() == sagaEvent;
    }

    @Test
    void sendSagaEventShouldHandleFailure() {
        SagaEvent sagaEvent = new SagaEvent();
        sagaEvent.setSagaId("inventory-saga-2");

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka failure"));

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        kafkaProducerService.sendSagaEvent(sagaEvent);

        verify(kafkaTemplate).send(eq("inventory-saga-topic"), eq("inventory-saga-2"), eq(sagaEvent));
    }
}
