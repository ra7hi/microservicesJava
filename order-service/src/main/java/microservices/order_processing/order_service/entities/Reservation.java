package microservices.order_processing.order_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservices.order_processing.order_service.enums.ReservationStatus;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sagaId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
