package microservices.order_processing.inventory_service.entities;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Long totalQuantity;

    @Column(nullable = false)
    private Long availableQuantity;

    @Column(nullable = false)
    private Double sale;
}
