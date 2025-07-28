package microservices.order_processing.order_service.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO-класс, описывающий заказ.
 * Содержит информацию об id-заказа, списке продуктов, общей стоимости, id-пользователя и дате заказа
 */
@Data
@Builder
public class OrderDto {
    private String orderId;
    private List<ProductDto> products;
    private Double totalPrice;
    private Long userId;
    private LocalDateTime orderDate;
}
