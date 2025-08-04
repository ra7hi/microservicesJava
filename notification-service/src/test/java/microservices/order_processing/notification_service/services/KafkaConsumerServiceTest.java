package microservices.order_processing.notification_service.services;


import microservices.order_processing.notification_service.dto.OrderDto;
import microservices.order_processing.notification_service.entities.Order;
import microservices.order_processing.notification_service.enums.SagaStatus;
import microservices.order_processing.notification_service.kafka.KafkaConsumerService;
import microservices.order_processing.notification_service.kafka.KafkaProducerService;
import microservices.order_processing.notification_service.saga.SagaEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

class KafkaConsumerServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleSagaEvents_shouldProcessOrderCreateEventSuccessfully() {
        OrderDto orderDto = OrderDto.builder()
                .orderId("order-123")
                .userId(1L)
                .totalPrice(100.0)
                .build();

        SagaEvent sagaEvent = new SagaEvent(
                "saga-1", "order.create", null, orderDto, null);

        Order savedOrder = Order.builder()
                .id("order-123")
                .userId(1L)
                .totalPrice(100.0)
                .build();

        when(orderService.saveOrder(orderDto)).thenReturn(savedOrder);

        kafkaConsumerService.handleSagaEvents(sagaEvent);

        verify(orderService).saveOrder(orderDto);

        ArgumentCaptor<SagaEvent> eventCaptor = ArgumentCaptor.forClass(SagaEvent.class);
        verify(kafkaProducerService).sendSagaEvent(eventCaptor.capture());

        SagaEvent sentEvent = eventCaptor.getValue();
        assert sentEvent.getEventType().equals("order.created");
        assert sentEvent.getStatus() == SagaStatus.ORDER_CREATED;
        assert sentEvent.getPayload() == orderDto;
    }

    @Test
    void handleSagaEvents_shouldHandleOrderCreateFailure() {
        OrderDto orderDto = OrderDto.builder()
                .orderId("order-124")
                .userId(2L)
                .totalPrice(200.0)
                .build();

        SagaEvent sagaEvent = new SagaEvent(
                "saga-2", "order.create", null, orderDto, null);

        when(orderService.saveOrder(orderDto))
                .thenThrow(new RuntimeException("DB error"));

        kafkaConsumerService.handleSagaEvents(sagaEvent);

        verify(orderService).saveOrder(orderDto);

        ArgumentCaptor<SagaEvent> eventCaptor = ArgumentCaptor.forClass(SagaEvent.class);
        verify(kafkaProducerService).sendSagaEvent(eventCaptor.capture());

        SagaEvent sentEvent = eventCaptor.getValue();
        assert sentEvent.getEventType().equals("order.creation.failed");
        assert sentEvent.getStatus() == SagaStatus.FAILED;
        assert sentEvent.getPayload() == null;
        assert sentEvent.getErrorMessage().equals("DB error");
    }

    @Test
    void handleSagaEvents_shouldIgnoreUnknownEventType() {
        SagaEvent unknownEvent = new SagaEvent(
                "saga-3", "unknown.event", null, null, null);

        kafkaConsumerService.handleSagaEvents(unknownEvent);

        verifyNoInteractions(orderService, kafkaProducerService);
    }
}

