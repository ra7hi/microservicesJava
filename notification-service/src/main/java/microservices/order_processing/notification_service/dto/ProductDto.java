package microservices.order_processing.notification_service.dto;

import lombok.*;

@Data
@Builder
public class ProductDto {
    private Long productId;
    private String name;
    private Double price;
    private Long quantity;
    private Double sale;
}
