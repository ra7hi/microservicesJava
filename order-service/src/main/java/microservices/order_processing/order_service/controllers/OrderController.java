package microservices.order_processing.order_service.controllers;

import lombok.RequiredArgsConstructor;
import microservices.order_processing.order_service.controllers.requests.OrderRequest;
import microservices.order_processing.order_service.controllers.responses.OrderResponse;
import microservices.order_processing.order_service.controllers.responses.OrderStatusResponse;
import microservices.order_processing.order_service.enums.OrderStatus;
import microservices.order_processing.order_service.impl.UserDetailsImpl;
import microservices.order_processing.order_service.services.OrderServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImp orderServiceImp;

    @PostMapping
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderServiceImp.processOrderCreation(userDetails.getUsername(), orderRequest);
        if(orderResponse.getOrderStatus() == OrderStatus.CREATED) {
            return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
        }
        else if(orderResponse.getOrderStatus() == OrderStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(orderResponse);
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> getOrderStatus(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String id) {
        OrderStatusResponse orderResponse = orderServiceImp.getOrderStatus(userDetails.getUsername(), id);
        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }
}
