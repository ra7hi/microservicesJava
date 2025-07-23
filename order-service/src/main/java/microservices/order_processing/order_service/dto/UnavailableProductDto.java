package microservices.order_processing.order_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnavailableProductDto {
    private long productId;
    private String reason;
    private Long requestedQuantity;
    private Long availableQuantity;
}
