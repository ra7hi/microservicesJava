package microservices.order_processing.order_service.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GrpcConfig {
    @Value("${grpc.inventory-service.host:localhost}")
    private String inventoryServiceHost;

    @Value("${grpc.inventory-service.port:9090}")
    private int inventoryServicePort;

    @Bean
    public ManagedChannel inventoryServiceChannel() {
        return ManagedChannelBuilder
                .forAddress(inventoryServiceHost, inventoryServicePort)
                .usePlaintext()
                .build();
    }

    @Bean
    public OrderServiceGrpc.OrderServiceBlockingStub inventoryServiceStub(ManagedChannel inventoryServiceChannel) {
        return OrderServiceGrpc.newBlockingStub(inventoryServiceChannel);
    }
}
