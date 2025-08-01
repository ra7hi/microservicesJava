package microservices.order_processing.order_service.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Kafka Producer для отправки сообщений.
 * <p>
 * Настраивает параметры подключения к Kafka, сериализацию ключей и значений,
 * а также параметры подтверждения доставки и повторных попыток.
 */
@Configuration
public class KafkaConfig {
    /**
     * Адреса Kafka bootstrap-серверов.
     * Значение подставляется из настроек приложения.
     */
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    /**
     * Создаёт фабрику продюсеров Kafka с необходимыми параметрами.
     * <p>
     * Включает:
     * <ul>
     *     <li>Серверы Kafka для подключения.</li>
     *     <li>Сериализацию ключей (строка).</li>
     *     <li>Сериализацию значений (в JSON).</li>
     *     <li>Требование подтверждений со стороны Kafka (acks=all) для надежности.</li>
     *     <li>Повторные попытки отправки (3 раза).</li>
     *     <li>Идемпотентность для избежания дублирования сообщений.</li>
     * </ul>
     *
     * @return фабрика продюсеров {@link ProducerFactory}
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Создаёт {@link KafkaTemplate} для отправки сообщений в Kafka.
     * <p>
     * Использует фабрику продюсеров с преднастроенными параметрами.
     *
     * @return экземпляр {@link KafkaTemplate}
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
