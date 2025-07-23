package microservices.order_processing.notification_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.order_processing.notification_service.entities.Order;
import microservices.order_processing.notification_service.enums.SagaStatus;
import microservices.order_processing.notification_service.saga.SagaEvent;
import microservices.order_processing.notification_service.services.OrderService;
import microservices.order_processing.notification_service.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final OrderService orderService;
    private final KafkaProducerService kafkaProducerService;

    // Слушатель для событий саги
    @KafkaListener(topics = "saga-events",
            groupId = "notification-service-saga-group",
            containerFactory = "sagaKafkaListenerContainerFactory")
    public void handleSagaEvents(SagaEvent sagaEvent) {
        log.info("Notification service received saga event: {}", sagaEvent);

        if ("order.create".equals(sagaEvent.getEventType())) {
            handleOrderCreation(sagaEvent);
        } else {
            log.warn("Unknown saga event type received: {}", sagaEvent.getEventType());
        }
    }


    @Transactional
    public void handleOrderCreation(SagaEvent sagaEvent) {
        try {
            OrderDto orderDto = (OrderDto) sagaEvent.getPayload();

            // Сохраняем заказ в БД
            Order savedOrder = orderService.saveOrder(orderDto);

            // Отправляем событие успешного создания заказа
            SagaEvent successEvent = new SagaEvent(
                    sagaEvent.getSagaId(), "order.created", SagaStatus.ORDER_CREATED,
                    orderDto, null);
            kafkaProducerService.sendSagaEvent(successEvent);

            log.info("Order created successfully for saga: {}, order ID: {}",
                    sagaEvent.getSagaId(), savedOrder.getId());

        } catch (Exception e) {
            log.error("Failed to create order for saga: {}", sagaEvent.getSagaId(), e);

            // Отправляем событие об ошибке создания заказа
            SagaEvent failureEvent = new SagaEvent(
                    sagaEvent.getSagaId(), "order.creation.failed", SagaStatus.FAILED,
                    null, e.getMessage());
            kafkaProducerService.sendSagaEvent(failureEvent);
        }
    }
}

