package microservices.order_processing.order_service.controllers.responses;

import lombok.Builder;
import lombok.Data;
import microservices.order_processing.order_service.enums.OrderStatus;

/**
 * DTO-Ответ на запрос пользователя о статусе заказа.
 * Содержит статус заказа
 */
@Builder
@Data
public class OrderStatusResponse {
    private OrderStatus orderStatus;
}
