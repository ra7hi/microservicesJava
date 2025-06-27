package microservices.order_processing.order_service.controllers;

import microservices.order_processing.order_service.controllers.requests.OrderRequest;
import microservices.order_processing.order_service.dto.ProductDto;
import microservices.order_processing.order_service.grpc.InventoryServiceClient;
import microservices.order_processing.order_service.impl.UserDetailsImpl;
import microservices.order_processing.order_service.kafka.KafkaProducerService;
import microservices.order_processing.order_service.repository.UsersRepository;
import microservices.order_processing.order_service.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final InventoryServiceClient inventoryServiceClient;
    private final KafkaProducerService kafkaProducerService;
    private final UsersRepository usersRepository;
    private final OrderService orderService;

    @Autowired
    public OrderController(InventoryServiceClient inventoryServiceClient,
                           KafkaProducerService kafkaProducerService,
                           UsersRepository usersRepository, OrderService orderService) {
        this.inventoryServiceClient = inventoryServiceClient;
        this.kafkaProducerService = kafkaProducerService;
        this.usersRepository = usersRepository;
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestBody OrderRequest orderRequest) {

        List<ProductDto> products = inventoryServiceClient.checkProductsAvailability(
                orderRequest.getProductIds());

        Long userId = usersRepository.findByUsername(userDetails.getUsername()).get().getId();

        List<ProductDto> availableProducts = products.stream()
                .filter(ProductDto::getProductAvailability)
                .toList();

        if (availableProducts.isEmpty()) {
            return ResponseEntity.badRequest().body("No available products");
        }

        kafkaProducerService.sendOrderEvent("order-created",
                orderService.createFinalOrder(userId, availableProducts));

        return ResponseEntity.status(HttpStatus.CREATED).body("Order created successfully");
    }
}
