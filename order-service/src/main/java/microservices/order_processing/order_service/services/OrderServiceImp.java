package microservices.order_processing.order_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.order_processing.order_service.controllers.requests.OrderRequest;
import microservices.order_processing.order_service.controllers.responses.OrderResponse;
import microservices.order_processing.order_service.controllers.responses.OrderStatusResponse;
import microservices.order_processing.order_service.dto.InventoryReservationDto;
import microservices.order_processing.order_service.dto.OrderDto;
import microservices.order_processing.order_service.dto.ProductDto;
import microservices.order_processing.order_service.entities.SagaState;
import microservices.order_processing.order_service.entities.Users;
import microservices.order_processing.order_service.enums.OrderStatus;
import microservices.order_processing.order_service.enums.SagaStatus;
import microservices.order_processing.order_service.exception.UserNotFoundException;
import microservices.order_processing.order_service.grpc.InventoryServiceClient;
import microservices.order_processing.order_service.kafka.KafkaProducerService;
import microservices.order_processing.order_service.repository.SagaStateRepository;
import microservices.order_processing.order_service.repository.UsersRepository;
import microservices.order_processing.order_service.saga.SagaEvent;
import microservices.order_processing.order_service.services.components.OrderMapper;
import microservices.order_processing.order_service.services.components.ProductDtoToProductReservationMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImp implements OrderService {

    private final UsersRepository usersRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final KafkaProducerService kafkaProducerService;
    private final OrderMapper orderMapper;
    private final SagaStateRepository sagaStateRepository;
    private final ProductDtoToProductReservationMapper productDtoToProductReservationMapper;
    private final UsersServiceImp userServiceImp;

    @Override
    public OrderResponse processOrderCreation(String username, OrderRequest orderRequest) {
        String sagaId = UUID.randomUUID().toString();
        String orderId = UUID.randomUUID().toString();
        orderRequest.setOrderId(orderId);

        Long userId = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found!"))
                .getId();

        OrderResponse orderResponse = inventoryServiceClient.checkProductsAvailability(orderRequest);

        if(!orderResponse.getUnavailableProducts().isEmpty()){
            orderResponse.setOrderStatus(OrderStatus.FAILED);
            return orderResponse;
        }

        SagaState sagaState = new SagaState(sagaId, orderId, userId, SagaStatus.STARTED, LocalDateTime.now(), LocalDateTime.now());
        sagaStateRepository.save(sagaState);

        InventoryReservationDto reservationDto = new InventoryReservationDto(
                sagaId, orderId, productDtoToProductReservationMapper.mapToProductReservations(orderResponse.getAvailableProducts()));

        SagaEvent sagaEvent = new SagaEvent(sagaId, "inventory.reserve", SagaStatus.STARTED, reservationDto, null);
        kafkaProducerService.sendSagaEvent(sagaEvent);

        orderResponse.setOrderStatus(OrderStatus.PENDING);
        return orderResponse;
    }

    @KafkaListener(topics = "saga-events", groupId = "order-service-saga-group")
    public void handleSagaEvents(SagaEvent sagaEvent) {
        log.info("Received saga event: {}", sagaEvent);

        switch (sagaEvent.getEventType()) {
            case "inventory.reserved":
                handleInventoryReserved(sagaEvent);
                break;
            case "order.created":
                handleOrderCreated(sagaEvent);
                break;
            case "inventory.reservation.failed":
            case "order.creation.failed":
                handleSagaFailure(sagaEvent);
                break;
            case "inventory.released":
                handleSagaCompensated(sagaEvent);
                break;
        }
    }

    private void handleInventoryReserved(SagaEvent sagaEvent) {
        SagaState sagaState = sagaStateRepository.findBySagaId(sagaEvent.getSagaId());
        sagaState.setStatus(SagaStatus.INVENTORY_RESERVED);
        sagaStateRepository.save(sagaState);

        OrderDto order = orderMapper.buildFinalOrder(
                sagaState.getUserId(),
                (List<ProductDto>) sagaEvent.getPayload(),
                sagaState.getOrderId()
        );

        SagaEvent createOrderEvent = new SagaEvent(
                sagaEvent.getSagaId(), "order.create", SagaStatus.INVENTORY_RESERVED, order, null);
        kafkaProducerService.sendSagaEvent(createOrderEvent);
    }
    private void handleOrderCreated(SagaEvent sagaEvent) {
        // Заказ создан успешно, завершаем сагу
        SagaState sagaState = sagaStateRepository.findBySagaId(sagaEvent.getSagaId());
        sagaState.setStatus(SagaStatus.COMPLETED);
        sagaStateRepository.save(sagaState);

        log.info("Saga completed successfully: {}", sagaEvent.getSagaId());
    }

    private void handleSagaFailure(SagaEvent sagaEvent) {
        // Начинаем компенсацию
        SagaState sagaState = sagaStateRepository.findBySagaId(sagaEvent.getSagaId());
        sagaState.setStatus(SagaStatus.COMPENSATING);
        sagaStateRepository.save(sagaState);

        if (sagaState.getStatus() == SagaStatus.INVENTORY_RESERVED) {
            // Если товары были зарезервированы, освобождаем их
            SagaEvent compensateEvent = new SagaEvent(
                    sagaEvent.getSagaId(), "inventory.release", SagaStatus.COMPENSATING,
                    sagaState.getOrderId(), sagaEvent.getErrorMessage());
            kafkaProducerService.sendSagaEvent(compensateEvent);
        }
    }

    private void handleSagaCompensated(SagaEvent sagaEvent) {
        SagaState sagaState = sagaStateRepository.findBySagaId(sagaEvent.getSagaId());
        sagaState.setStatus(SagaStatus.COMPENSATED);
        sagaStateRepository.save(sagaState);

        log.info("Saga compensated: {}", sagaEvent.getSagaId());
    }

    public OrderStatusResponse getOrderStatus(String username, String OrderId) {
        Optional<Users> user = userServiceImp.findUserByUsername(username);
        if(user.isEmpty()){
            throw new UserNotFoundException("User not found!");
        }
        SagaState sagaState = sagaStateRepository.findByOrderIdAndUserId(OrderId, user.get().getId());
        switch (sagaState.getStatus()) {
            case INVENTORY_RESERVED:
                return OrderStatusResponse.builder().orderStatus(OrderStatus.PENDING).build();
            case COMPENSATING:
                case COMPENSATED:
                    return OrderStatusResponse.builder().orderStatus(OrderStatus.FAILED).build();
            case COMPLETED:
                return OrderStatusResponse.builder().orderStatus(OrderStatus.CREATED).build();
                default: return OrderStatusResponse.builder().orderStatus(OrderStatus.PENDING).build();
        }
    }
}
