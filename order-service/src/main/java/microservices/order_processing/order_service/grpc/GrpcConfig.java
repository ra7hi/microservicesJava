package microservices.order_processing.order_service.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import microservices.order_processing.order_service.grpc.OrderServiceGrpc;

/**
 * Конфигурация для настройки gRPC-соединения с сервисом управления запасами (Inventory Service).
 */
@Configuration
public class GrpcConfig {
    @Value("${grpc.inventory-service.host:localhost}")
    private String inventoryServiceHost;

    @Value("${grpc.inventory-service.port:9090}")
    private int inventoryServicePort;

    /**
     * Создает gRPC-канал для соединения с inventory-service.
     *
     * @return настроенный {@link ManagedChannel}
     */
    @Bean
    public ManagedChannel inventoryServiceChannel() {
        return ManagedChannelBuilder
                .forAddress(inventoryServiceHost, inventoryServicePort)
                .usePlaintext()
                .build();
    }

    /**
     * Создает blocking stub для обращения к методам inventory-service по gRPC.
     *
     * @param inventoryServiceChannel gRPC-канал, связанный с inventory-service
     * @return {@link OrderServiceGrpc.OrderServiceBlockingStub} для вызова методов сервиса
     */
    @Bean
    public OrderServiceGrpc.OrderServiceBlockingStub inventoryServiceStub(ManagedChannel inventoryServiceChannel) {
        return OrderServiceGrpc.newBlockingStub(inventoryServiceChannel);
    }
}
