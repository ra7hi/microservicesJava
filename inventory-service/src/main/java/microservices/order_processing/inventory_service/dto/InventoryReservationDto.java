package microservices.order_processing.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservices.order_processing.inventory_service.saga.ProductReservation;

import java.util.List;

/**
 * DTO-класс, описывающий резервирование товаров на складе в рамках саги.
 * <p>Используется для передачи информации о заказе и товарах, подлежащих резервированию,
 * между сервисами в Saga.</p>
 * @see ProductReservation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReservationDto {
    private String sagaId;
    private String orderId;
    private List<ProductReservation> products;
}