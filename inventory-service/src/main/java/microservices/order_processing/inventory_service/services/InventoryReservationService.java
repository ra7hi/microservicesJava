package microservices.order_processing.inventory_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.order_processing.inventory_service.dto.InventoryReservationDto;
import microservices.order_processing.inventory_service.entities.ProductEntity;
import microservices.order_processing.inventory_service.entities.Reservation;
import microservices.order_processing.inventory_service.enums.ReservationStatus;
import microservices.order_processing.inventory_service.enums.SagaStatus;
import microservices.order_processing.inventory_service.exceptions.InsufficientInventoryException;
import microservices.order_processing.inventory_service.exceptions.ProductNotFoundException;
import microservices.order_processing.inventory_service.kafka.KafkaProducerService;
import microservices.order_processing.inventory_service.repositories.ProductRepository;
import microservices.order_processing.inventory_service.repositories.ReservationRepository;
import microservices.order_processing.inventory_service.saga.ProductReservation;
import microservices.order_processing.inventory_service.saga.SagaEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис получения событий из Kafka в топике saga-events и управления резервацией товаров в рамках саги.
 * Публикует событие о статусе резервирования
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationService {

    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Обработка входящих событий саги из Kafka.
     * @param sagaEvent событие саги из Kafka
     */
    @KafkaListener(topics = "saga-events", groupId = "inventory-service-saga-group")
    public void handleSagaEvents(SagaEvent sagaEvent) {
        log.info("Inventory service received saga event: {}", sagaEvent);

        switch (sagaEvent.getEventType()) {
            case "inventory.reserve":
                handleInventoryReservation(sagaEvent);
                break;
            case "inventory.release":
                handleInventoryRelease(sagaEvent);
                break;
            case "order.created":
                handleOrderCreatedConfirmation(sagaEvent);
                break;
        }
    }

    /**
     * Обрабатывает запрос на резервирование товаров.
     * Создается запись о резервации и отправляет событие об успешном или провальном резервировании
     * @param sagaEvent событие саги с данными резервирования
     */
    @Transactional
    public void handleInventoryReservation(SagaEvent sagaEvent) {
        try {
            InventoryReservationDto reservationDto = (InventoryReservationDto) sagaEvent.getPayload();

            // Резервируем товары
            List<ProductReservation> reservations = new ArrayList<>();
            for (ProductReservation productReservation : reservationDto.getProducts()) {
                ProductEntity product = productRepository.findById(productReservation.getProductId())
                        .orElseThrow(() -> new ProductNotFoundException("Product not found"));

                if (product.getAvailableQuantity() < productReservation.getQuantity()) {
                    throw new InsufficientInventoryException("Insufficient inventory");
                }

                // Резервируем товар (уменьшаем доступное количество, создаем запись резервации)
                product.setAvailableQuantity(product.getAvailableQuantity() - productReservation.getQuantity());
                productRepository.save(product);

                Reservation reservation = new Reservation(
                        sagaEvent.getSagaId(),
                        product.getId(),
                        productReservation.getQuantity(),
                        ReservationStatus.RESERVED
                );
                reservationRepository.save(reservation);
                reservations.add(productReservation);
            }

            // Отправляем событие успешного резервирования
            SagaEvent successEvent = new SagaEvent(
                    sagaEvent.getSagaId(), "inventory.reserved", SagaStatus.INVENTORY_RESERVED,
                    reservations, null);
            kafkaProducerService.sendSagaEvent(successEvent);

        } catch (Exception e) {
            log.error("Failed to reserve inventory for saga: {}", sagaEvent.getSagaId(), e);

            SagaEvent failureEvent = new SagaEvent(
                    sagaEvent.getSagaId(), "inventory.reservation.failed", SagaStatus.FAILED,
                    null, e.getMessage());
            kafkaProducerService.sendSagaEvent(failureEvent);
        }
    }

    /**
     * Обрабатывает запрос снятия товара с резерва и увеличивает количество доступных товаров.
     * Отправляет событие в Kafka об успешном освобождении зарезервированных товаров
     * @param sagaEvent событие саги с запросом на освобождение
     */
    @Transactional
    public void handleInventoryRelease(SagaEvent sagaEvent) {
        try {
            String sagaId = sagaEvent.getSagaId();
            List<Reservation> reservations = reservationRepository.findBySagaId(sagaId);

            // Освобождаем зарезервированные товары
            for (Reservation reservation : reservations) {
                ProductEntity product = productRepository.findById(reservation.getProductId())
                        .orElseThrow(() -> new ProductNotFoundException("Product not found"));

                product.setAvailableQuantity(product.getAvailableQuantity() + reservation.getQuantity());
                productRepository.save(product);

                reservation.setStatus(ReservationStatus.RELEASED);
                reservationRepository.save(reservation);
            }

            SagaEvent releasedEvent = new SagaEvent(
                    sagaId, "inventory.released", SagaStatus.COMPENSATED, null, null);
            kafkaProducerService.sendSagaEvent(releasedEvent);

        } catch (Exception e) {
            log.error("Failed to release inventory for saga: {}", sagaEvent.getSagaId(), e);
        }
    }

    /**
     * Совершает окончательное списание товаров при получении события об успешном создании заказа
     * @param sagaEvent событие саги с подтверждением создания заказа
     */
    @Transactional
    public void handleOrderCreatedConfirmation(SagaEvent sagaEvent) {
        // Заказ создан успешно, подтверждаем резервацию (делаем списание)
        String sagaId = sagaEvent.getSagaId();
        List<Reservation> reservations = reservationRepository.findBySagaId(sagaId);

        for (Reservation reservation : reservations) {
            ProductEntity product = productRepository.findById(reservation.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found"));

            // Уменьшаем общее количество товара (окончательное списание)
            product.setTotalQuantity(product.getTotalQuantity() - reservation.getQuantity());
            productRepository.save(product);

            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
        }

        log.info("Inventory confirmed for saga: {}", sagaId);
    }
}