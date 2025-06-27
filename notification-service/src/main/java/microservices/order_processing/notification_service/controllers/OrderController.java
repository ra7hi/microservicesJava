package microservices.order_processing.notification_service.controllers;

import microservices.order_processing.notification_service.OrderService.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getALlOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{order_id}")
    public ResponseEntity<?> getOrdersById(@PathVariable Long order_id) {
        return ResponseEntity.ok(orderService.getAllOrdersByOrderId(order_id));
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long user_id) {
        return ResponseEntity.ok(orderService.getAllOrdersByUserId(user_id));
    }
}
