package microservices.order_processing.order_service.controllers.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO-запрос для добавления продукта в заказ.
 * Используется при создании или обновлении заказа пользователем.
 * Содержит ID продукта и его количество.
 * Валидируется через Bean Validation.
 */
@Getter
@Setter
public class ProductOrderRequest {
    @NotNull(message = "productId cannot be null")
    Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be 0 or more")
    Long quantity;
}
