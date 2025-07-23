package microservices.order_processing.inventory_service.repositories;

import microservices.order_processing.inventory_service.entities.Reservation;
import microservices.order_processing.inventory_service.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findBySagaId(String sagaId);
    List<Reservation> findBySagaIdAndStatus(String sagaId, ReservationStatus status);
}
