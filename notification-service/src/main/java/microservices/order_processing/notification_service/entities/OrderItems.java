package microservices.order_processing.notification_service.entities;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "order_items")
@Builder
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double sale;
}
