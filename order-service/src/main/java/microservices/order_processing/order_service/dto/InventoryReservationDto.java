package microservices.order_processing.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import microservices.order_processing.order_service.saga.ProductReservation;
import org.checkerframework.checker.units.qual.N;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReservationDto {
    private String sagaId;
    private String orderId;
    private List<ProductReservation> products;
}
