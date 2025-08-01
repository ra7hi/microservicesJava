package microservices.order_processing.notification_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Сущность заказа, отображаемая на таблицу {@code orders} в БД.
 * Содержит основную информацию о заказе: состав {@link OrderItems}, общая стоимость, идентификатор пользователя и дата оформления.
 * Используется для хранения заказов.
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    private String id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderItems> orderItems;

    private Double totalPrice;

    private Long userId;

    private LocalDateTime orderDate;
}
