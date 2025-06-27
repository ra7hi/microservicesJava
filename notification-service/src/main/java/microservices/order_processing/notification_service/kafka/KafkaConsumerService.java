package microservices.order_processing.notification_service.kafka;

import microservices.order_processing.notification_service.OrderService.OrderService;
import microservices.order_processing.notification_service.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
public class KafkaConsumerService {

    private final OrderService orderService;

    @Autowired
    public KafkaConsumerService(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "order-events", groupId = "order-service-group")
    public void handleOrderEvents(
            @Payload OrderDto message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            log.info("Received order event - Topic: {}, Key: {}, Partition: {}, Offset: {}, Message: {}",
                    topic, key, partition, offset, message);
            orderService.saveOrder(message);

        } catch (Exception e) {
            log.error("Error processing order event: {}", e.getMessage(), e);
        }
    }
}
