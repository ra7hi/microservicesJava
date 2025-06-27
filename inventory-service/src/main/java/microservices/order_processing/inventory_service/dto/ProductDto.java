package microservices.order_processing.inventory_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Price must be zero or positive")
    private Double price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be 0 or more")
    private Long quantity;

    @NotNull(message = "Sale must be filled in")
    @DecimalMin(value = "0.0", inclusive = true, message = "Sale must be at least 0.0")
    @DecimalMax(value = "1.0", inclusive = true, message = "Sale must be at most 1.0")
    private Double sale;
}
