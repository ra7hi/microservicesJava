package microservices.order_processing.order_service.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ProductDto {
    private Long productId;
    private String name;
    private Double price;
    private Long quantity;
    private Double sale;
}
