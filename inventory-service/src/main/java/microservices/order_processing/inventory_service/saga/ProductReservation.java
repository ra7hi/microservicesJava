package microservices.order_processing.inventory_service.saga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для представления резервации продукта в рамках саги.
 * Содержит идентификатор продукта и количество зарезервированного товара.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReservation {
    private Long productId;
    private Long quantity;
}

