package microservices.order_processing.order_service.dto;

import lombok.Data;
import lombok.Builder;

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
