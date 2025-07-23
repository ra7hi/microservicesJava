package microservices.order_processing.notification_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

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
