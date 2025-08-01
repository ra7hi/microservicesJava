package microservices.order_processing.order_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import microservices.order_processing.order_service.saga.SagaEvent;

/**
 * Сервис для отправки событий саги в Kafka.
 * <p>
 * Использует {@link KafkaTemplate} для асинхронной отправки сообщений в топик,
 * указанный в конфигурации.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    /**
     * Имя топика для событий саги.
     * Значение подставляется из настроек приложения (application.properties или application.yml).
     */
    @Value("${kafka.topics.saga-events}")
    private String sagaEventsTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Отправляет событие саги в Kafka в указанный топик.
     * <p>
     * Использует ключ сообщения, равный ID саги, для обеспечения корректной маршрутизации и
     * упорядочивания сообщений по ключу.
     * <p>
     * Логирует успешную отправку или ошибку в случае неудачи.
     *
     * @param sagaEvent объект события саги для отправки
     */
    public void sendSagaEvent(SagaEvent sagaEvent) {
        log.info("Sending saga event: {}", sagaEvent);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(sagaEventsTopic, sagaEvent.getSagaId(), sagaEvent);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Saga event sent successfully: {}", sagaEvent.getSagaId());
            } else {
                log.error("Failed to send saga event: {}, error: {}", sagaEvent.getSagaId(), ex.getMessage());
            }
        });
    }
}

