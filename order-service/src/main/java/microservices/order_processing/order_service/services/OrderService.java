package microservices.order_processing.order_service.services;

import microservices.order_processing.order_service.controllers.requests.OrderRequest;
import microservices.order_processing.order_service.dto.OrderDto;
import microservices.order_processing.order_service.dto.ProductDto;
import microservices.order_processing.order_service.exception.NoAvailableProductsException;
import microservices.order_processing.order_service.exception.UserNotFoundException;
import microservices.order_processing.order_service.grpc.InventoryServiceClient;
import microservices.order_processing.order_service.kafka.KafkaProducerService;
import microservices.order_processing.order_service.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final UsersRepository usersRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public OrderService(UsersRepository usersRepository, InventoryServiceClient inventoryServiceClient, KafkaProducerService kafkaProducerService) {
        this.usersRepository = usersRepository;
        this.inventoryServiceClient = inventoryServiceClient;
        this.kafkaProducerService = kafkaProducerService;
    }

    public void processOrderCreation(String username, OrderRequest orderRequest) {
        Long userId = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found!"))
                .getId();

        List<ProductDto> products = inventoryServiceClient.checkProductsAvailability(orderRequest.getProductIds());

        List<ProductDto> availableProducts = products.stream()
                .filter(ProductDto::getProductAvailability)
                .toList();

        if (availableProducts.isEmpty()) {
            throw new NoAvailableProductsException("No available products!");
        }

        OrderDto order = buildFinalOrder(userId, availableProducts);

        kafkaProducerService.sendOrderEvent("order-created", order);
    }

    public OrderDto buildFinalOrder(Long userId, List<ProductDto> availableProducts) {

        double totalPrice = availableProducts.stream()
                .mapToDouble(p -> p.getPrice() - p.getPrice() * p.getSale())
                .sum();

       return OrderDto.builder()
                .orderId(UUID.randomUUID().toString())
                .userId(userId)
                .products(availableProducts)
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .build();
    }
}
