package microservices.order_processing.notification_service.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
/**
 * DTO для представления информации о заказе, используемой в notification-service.
 * Содержит данные, необходимые для сохранения заказа.
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
