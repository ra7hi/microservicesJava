package microservices.order_processing.inventory_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservices.order_processing.inventory_service.enums.ReservationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saga_id", nullable = false)
    private String sagaId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Reservation(String sagaId, Long productId, Long quantity, ReservationStatus status) {
        this.sagaId = sagaId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}
