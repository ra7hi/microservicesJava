package microservices.order_processing.order_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Tag(name = "Order", description = "API для создания заказов и получения статуса заказа")
public class OrderController {

    private final OrderServiceImp orderServiceImp;

    @Operation(
            summary = "Создание нового заказа",
            description = "Создает заказ от имени аутентифицированного пользователя с указанным списком продуктов."
    )
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные заказа для создания",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody OrderRequest orderRequest) {

        OrderResponse orderResponse = orderServiceImp.processOrderCreation(userDetails.getUsername(), orderRequest);
        if(orderResponse.getOrderStatus() == OrderStatus.CREATED) {
            return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
        } else if(orderResponse.getOrderStatus() == OrderStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(orderResponse);
        }
    }

    @Operation(
            summary = "Получить статус заказа",
            description = "Возвращает статус заказа по идентификатору для аутентифицированного пользователя."
    )
    @GetMapping("/{id}/status")
    public ResponseEntity<OrderStatusResponse> getOrderStatus(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String id) {

        OrderStatusResponse orderResponse = orderServiceImp.getOrderStatus(userDetails.getUsername(), id);
        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }
}
