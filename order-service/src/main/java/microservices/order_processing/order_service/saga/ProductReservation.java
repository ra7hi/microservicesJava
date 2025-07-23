package microservices.order_processing.order_service.saga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReservation {
    private Long productId;
    private Long quantity;
}
