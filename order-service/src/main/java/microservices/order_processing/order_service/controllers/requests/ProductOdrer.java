package microservices.order_processing.order_service.controllers.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductOdrer {
    @NotNull(message = "productId cannot be null")
    Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be 0 or more")
    Long quantity;
}
