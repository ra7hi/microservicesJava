package microservices.order_processing.order_service.controllers.responses;

import lombok.Builder;
import lombok.Data;
import microservices.order_processing.order_service.dto.ProductDto;
import microservices.order_processing.order_service.dto.UnavailableProductDto;
import microservices.order_processing.order_service.enums.OrderStatus;

import java.util.List;

/**
 * DTO-ответ на создание заказа.
 * Содержит статус заказа, список продуктов, доступные для заказа,
 * а также список продуктов, недоступных для заказа
 */
@Builder
@Data
public class OrderResponse {
    private OrderStatus orderStatus;
    private List<ProductDto> availableProducts;
    private List<UnavailableProductDto> unavailableProducts;
}
