package microservices.order_processing.notification_service.dto;

import lombok.Data;
import lombok.Builder;

/**
 * DTO-класс, представляющий продукт.
 * Используется в составе заказа при его сохранении.
 */
@Data
@Builder
public class ProductDto {
    private Long productId;
    private String name;
    private Double price;
    private Long quantity;
    private Double sale;
}
