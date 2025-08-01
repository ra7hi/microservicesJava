package microservices.order_processing.inventory_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Сущность-класс, представляющий продукт в базе данных.
 * <p>Используется для хранения и управления информацией о продуктах в inventory-сервисе.</p>
 * <p>Связан с таблицей {@code product}. Все поля помечены как обязательные и не допускают {@code null}-значений.</p>
 */
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
