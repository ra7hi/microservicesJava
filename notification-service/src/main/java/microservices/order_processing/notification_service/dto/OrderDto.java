package microservices.order_processing.notification_service.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDto {
    private String orderId;
    private List<ProductDto> products;
    private Double totalPrice;
    private Long userId;
    private LocalDateTime orderDate;
}
