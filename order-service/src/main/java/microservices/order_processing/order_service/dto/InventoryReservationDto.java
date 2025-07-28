package microservices.order_processing.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservices.order_processing.order_service.saga.ProductReservation;

import java.util.List;

/**
 * DTO-Класс, описывающий резервирование товаров для заказа.
 * Содержит id-саги, id-заказа и список продуктов, которые необходимо зарезервировать в inventory-service
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReservationDto {
    private String sagaId;
    private String orderId;
    private List<ProductReservation> products;
}
