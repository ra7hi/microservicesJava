package microservices.order_processing.inventory_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * DTO-класс, представляющий продукт в сервисе инвентаря.
 * <p>Используется при создании, обновлении и валидации данных о продукте.
 * <p>Все поля снабжены аннотациями валидации Bean Validation для обеспечения корректности данных.</p>
 */
@Data
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
    private Long availableQuantity;

    @NotNull(message = "Sale must be filled in")
    @DecimalMin(value = "0.0", inclusive = true, message = "Sale must be at least 0.0")
    @DecimalMax(value = "1.0", inclusive = true, message = "Sale must be at most 1.0")
    private Double sale;
}
