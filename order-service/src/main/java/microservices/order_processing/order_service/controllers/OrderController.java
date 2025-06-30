package microservices.order_processing.order_service.controllers;

import microservices.order_processing.order_service.controllers.requests.OrderRequest;
import microservices.order_processing.order_service.impl.UserDetailsImpl;
import microservices.order_processing.order_service.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestBody OrderRequest orderRequest) {
        orderService.processOrderCreation(userDetails.getUsername(), orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order created successfully");
    }
}
