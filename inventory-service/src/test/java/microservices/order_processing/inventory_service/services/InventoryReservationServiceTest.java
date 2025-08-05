package microservices.order_processing.inventory_service.services;

import microservices.order_processing.inventory_service.dto.InventoryReservationDto;
import microservices.order_processing.inventory_service.entities.ProductEntity;
import microservices.order_processing.inventory_service.entities.Reservation;
import microservices.order_processing.inventory_service.enums.ReservationStatus;
import microservices.order_processing.inventory_service.enums.SagaStatus;
import microservices.order_processing.inventory_service.kafka.KafkaProducerService;
import microservices.order_processing.inventory_service.repositories.ProductRepository;
import microservices.order_processing.inventory_service.repositories.ReservationRepository;
import microservices.order_processing.inventory_service.saga.ProductReservation;
import microservices.order_processing.inventory_service.saga.SagaEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryReservationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private InventoryReservationService inventoryReservationService;

    @Test
    void testHandleInventoryReservationSuccess() {
        String sagaId = "saga-123";
        long productId = 1L;
        long quantity = 5L;

        ProductReservation productReservation = new ProductReservation(productId, quantity);
        InventoryReservationDto reservationDto = InventoryReservationDto.builder()
                .products(List.of(productReservation))
                .build();

        SagaEvent sagaEvent = new SagaEvent(sagaId, "inventory.reserve", null, reservationDto, null);

        ProductEntity product = ProductEntity.builder()
                .id(productId)
                .availableQuantity(10L)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        inventoryReservationService.handleInventoryReservation(sagaEvent);

        verify(productRepository).save(argThat(p -> p.getAvailableQuantity() == 5L));
        verify(reservationRepository).save(any(Reservation.class));
        verify(kafkaProducerService).sendSagaEvent(argThat(event ->
                event.getEventType().equals("inventory.reserved")
                        && event.getSagaId().equals(sagaId)
                        && event.getStatus() == SagaStatus.INVENTORY_RESERVED));
    }

    @Test
    void testHandleInventoryReservationInsufficientInventory() {
        String sagaId = "saga-456";
        long productId = 2L;
        long quantity = 10L;

        ProductReservation productReservation = new ProductReservation(productId, quantity);
        InventoryReservationDto reservationDto = InventoryReservationDto.builder().products(List.of(productReservation)).build();

        SagaEvent sagaEvent = new SagaEvent(sagaId, "inventory.reserve", null, reservationDto, null);

        ProductEntity product = ProductEntity.builder()
                .id(productId)
                .availableQuantity(5L)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        inventoryReservationService.handleInventoryReservation(sagaEvent);

        verify(kafkaProducerService).sendSagaEvent(argThat(event ->
                event.getEventType().equals("inventory.reservation.failed")
                        && event.getSagaId().equals(sagaId)
                        && event.getStatus() == SagaStatus.FAILED));
    }

    @Test
    void testHandleInventoryReleaseSuccess() {
        String sagaId = "saga-789";
        SagaEvent sagaEvent = new SagaEvent(sagaId, "inventory.release", null, null, null);

        Reservation reservation = new Reservation(sagaId, 1L, 3L, ReservationStatus.RESERVED);
        ProductEntity product = ProductEntity.builder()
                .id(1L)
                .availableQuantity(2L)
                .build();

        when(reservationRepository.findBySagaId(sagaId)).thenReturn(List.of(reservation));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        inventoryReservationService.handleInventoryRelease(sagaEvent);

        verify(productRepository).save(argThat(p -> p.getAvailableQuantity() == 5L));
        verify(reservationRepository).save(argThat(r -> r.getStatus() == ReservationStatus.RELEASED));
        verify(kafkaProducerService).sendSagaEvent(argThat(event ->
                event.getEventType().equals("inventory.released")
                        && event.getStatus() == SagaStatus.COMPENSATED));
    }

    @Test
    void testHandleOrderCreatedConfirmationSuccess() {
        String sagaId = "saga-999";
        SagaEvent sagaEvent = new SagaEvent(sagaId, "order.created", null, null, null);

        Reservation reservation = new Reservation(sagaId, 1L, 2L, ReservationStatus.RESERVED);
        ProductEntity product = ProductEntity.builder()
                .id(1L)
                .totalQuantity(10L)
                .build();

        when(reservationRepository.findBySagaId(sagaId)).thenReturn(List.of(reservation));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        inventoryReservationService.handleOrderCreatedConfirmation(sagaEvent);

        verify(productRepository).save(argThat(p -> p.getTotalQuantity() == 8L));
        verify(reservationRepository).save(argThat(r -> r.getStatus() == ReservationStatus.CONFIRMED));
    }
}



