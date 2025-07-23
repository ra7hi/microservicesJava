package microservices.order_processing.notification_service.kafka;

import microservices.order_processing.notification_service.dto.OrderDto;
import microservices.order_processing.notification_service.saga.SagaEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    // Producer конфигурация
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Consumer конфигурация для SagaEvent
    @Bean
    public ConsumerFactory<String, SagaEvent> sagaEventConsumerFactory() {
        JsonDeserializer<SagaEvent> deserializer = new JsonDeserializer<>(SagaEvent.class);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.setUseTypeMapperForKey(false);
        deserializer.setUseTypeHeaders(false);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(deserializer));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SagaEvent> sagaKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SagaEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sagaEventConsumerFactory());
        return factory;
    }

    // Оригинальная consumer конфигурация для OrderDto (если нужна)
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderDto> kafkaListenerContainerFactory(
            ConsumerFactory<String, OrderDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderDto> consumerFactory() {
        JsonDeserializer<OrderDto> deserializer = new JsonDeserializer<>(OrderDto.class);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.setUseTypeMapperForKey(false);
        deserializer.setUseTypeHeaders(false);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(deserializer));
    }
}

