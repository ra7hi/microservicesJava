package microservices.order_processing.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservices.order_processing.inventory_service.saga.ProductReservation;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReservationDto {
    private String sagaId;
    private String orderId;
    private List<ProductReservation> products;
}