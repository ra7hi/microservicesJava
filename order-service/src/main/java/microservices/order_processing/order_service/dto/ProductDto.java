package microservices.order_processing.order_service.dto;

import lombok.*;

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
