package microservices.order_processing.inventory_service.repositories;

import microservices.order_processing.inventory_service.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findProductById(Long id);
}
