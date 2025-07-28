package microservices.order_processing.order_service.dto;

import lombok.Builder;
import lombok.Data;
import microservices.order_processing.order_service.controllers.responses.OrderResponse;

/**
 * DTO-класс, который описывает продукт, недоступный для заказа.
 * Содержит id-продукта, причину недоступности, запрашиваемое количество товара и доступное количество товара.
 * Используется в {@link OrderResponse}
 */
@Data
@Builder
public class UnavailableProductDto {
    private long productId;
    private String reason;
    private Long requestedQuantity;
    private Long availableQuantity;
}
