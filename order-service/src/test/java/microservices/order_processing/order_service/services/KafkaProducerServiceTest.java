package microservices.order_processing.order_service.services;


import microservices.order_processing.order_service.kafka.KafkaProducerService;
import microservices.order_processing.order_service.saga.SagaEvent;
import microservices.order_processing.order_service.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
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
        TestUtils.setField(kafkaProducerService, "sagaEventsTopic", "test-saga-topic");
    }

    @Test
    void sendSagaEventShouldSendEventSuccessfully() {
        SagaEvent sagaEvent = new SagaEvent();
        sagaEvent.setSagaId("saga-123");

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        kafkaProducerService.sendSagaEvent(sagaEvent);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());

        String capturedTopic = topicCaptor.getValue();
        String capturedKey = keyCaptor.getValue();
        Object capturedValue = valueCaptor.getValue();

        assert capturedTopic.equals("test-saga-topic");
        assert capturedKey.equals("saga-123");
        assert capturedValue == sagaEvent;
    }

    @Test
    void sendSagaEventShouldLogErrorOnFailure() {
        SagaEvent sagaEvent = new SagaEvent();
        sagaEvent.setSagaId("saga-456");

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka send failed"));

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        kafkaProducerService.sendSagaEvent(sagaEvent);

        verify(kafkaTemplate).send(eq("test-saga-topic"), eq("saga-456"), eq(sagaEvent));
    }
}

