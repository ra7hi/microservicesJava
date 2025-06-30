package microservices.order_processing.order_service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long productId;
    private Boolean productAvailability;
    private String name;
    private Double price;
    private Long quantity;
    private Double sale;
}
