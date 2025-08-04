package microservices.order_processing.notification_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import microservices.order_processing.notification_service.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Контроллер для работы с заказами в микросервисе уведомлений.
 */
@Tag(name = "Уведомления о заказах", description = "Операции получения информации о заказах для уведомлений")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Получить список всех заказов.
     *
     * @return список всех заказов
     */
    @Operation(summary = "Получить все заказы", description = "Возвращает список всех заказов в системе.")
    @ApiResponse(responseCode = "200", description = "Список заказов успешно получен")
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Получить заказ по ID заказа.
     *
     * @param order_id идентификатор заказа
     * @return заказ
     */
    @Operation(summary = "Получить заказ по ID", description = "Возвращает информацию о заказе по его уникальному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ найден"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    @GetMapping("/id/{order_id}")
    public ResponseEntity<?> getOrderById(
            @Parameter(description = "ID заказа", example = "ORD123456")
            @PathVariable String order_id) {
        return ResponseEntity.ok(orderService.getOrderByOrderId(order_id));
    }

    /**
     * Получить все заказы пользователя по его ID.
     *
     * @param user_id идентификатор пользователя
     * @return список заказов пользователя
     */
    @Operation(summary = "Получить заказы пользователя", description = "Возвращает все заказы, сделанные пользователем по его ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказы пользователя получены"),
            @ApiResponse(responseCode = "404", description = "Пользователь или заказы не найдены")
    })
    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrdersByUserId(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long user_id) {
        return ResponseEntity.ok(orderService.getAllOrdersByUserId(user_id));
    }
}
