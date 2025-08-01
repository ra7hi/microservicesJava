package microservices.order_processing.notification_service.repositories;

import microservices.order_processing.notification_service.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findOrderById(String orderId);
    List<Order> findOrdersByUserId(Long userId);
}
