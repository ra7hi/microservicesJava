package microservices.order_processing.order_service.dto;

import lombok.Data;
import lombok.Builder;

/**
 * DTO-класс, описывающий продукт заказа.
 * Содержит id-продукта, наименование, цену, количество продукта, а также размер скидки в виде вещественного числа (от 0.00 до 1.00)
 */
@Data
@Builder
public class ProductDto {
    private Long productId;
    private String name;
    private Double price;
    private Long quantity;
    private Double sale;
}
