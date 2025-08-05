package microservices.order_processing.order_service.services;

import microservices.order_processing.order_service.controllers.responses.OrderStatusResponse;
import microservices.order_processing.order_service.dto.OrderDto;
import microservices.order_processing.order_service.dto.ProductDto;
import microservices.order_processing.order_service.dto.UnavailableProductDto;
import microservices.order_processing.order_service.enums.SagaStatus;
import microservices.order_processing.order_service.exception.UserNotFoundException;
import microservices.order_processing.order_service.grpc.InventoryServiceClient;
import microservices.order_processing.order_service.kafka.KafkaProducerService;
import microservices.order_processing.order_service.saga.ProductReservation;
import microservices.order_processing.order_service.saga.SagaEvent;
import microservices.order_processing.order_service.services.components.OrderMapper;
import microservices.order_processing.order_service.services.components.ProductDtoToProductReservationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import microservices.order_processing.order_service.controllers.requests.OrderRequest;
import microservices.order_processing.order_service.controllers.responses.OrderResponse;
import microservices.order_processing.order_service.entities.SagaState;
import microservices.order_processing.order_service.entities.Users;
import microservices.order_processing.order_service.enums.OrderStatus;
import microservices.order_processing.order_service.repository.SagaStateRepository;
import microservices.order_processing.order_service.repository.UsersRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;

@ExtendWith(MockitoExtension.class)
class OrderServiceImpTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private SagaStateRepository sagaStateRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UsersServiceImp usersServiceImp;

    @InjectMocks
    private OrderServiceImp orderService;

    @Mock
    private ProductDtoToProductReservationMapper productDtoToProductReservationMapper;

        @Test
        void processOrderCreationAllProductsAvailableReturnsPendingOrder() {
            String username = "testUser";
            String orderId = UUID.randomUUID().toString();
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setOrderId(orderId);

            Users user = new Users();
            user.setId(1L);

            OrderResponse inventoryResponse = new OrderResponse();
            inventoryResponse.setAvailableProducts(List.of(new ProductDto()));
            inventoryResponse.setUnavailableProducts(List.of());

            when(usersRepository.findByUsername(username))
                    .thenReturn(Optional.of(user));
            when(inventoryServiceClient.checkProductsAvailability(orderRequest))
                    .thenReturn(inventoryResponse);
            when(sagaStateRepository.save(any(SagaState.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(productDtoToProductReservationMapper.mapToProductReservations(anyList()))
                    .thenReturn(List.of(new ProductReservation()));

            OrderResponse response = orderService.processOrderCreation(username, orderRequest);

            assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
            verify(kafkaProducerService).sendSagaEvent(any(SagaEvent.class));
            verify(sagaStateRepository).save(any(SagaState.class));
        }

    @Test
    void processOrderCreationWithUnavailableProductsReturnsFailedStatus() {
        OrderRequest orderRequest = new OrderRequest();
        OrderResponse inventoryResponse = OrderResponse.builder()
                .unavailableProducts(List.of(
                        UnavailableProductDto.builder()
                                .productId(1L)
                                .reason("Out of stock")
                                .requestedQuantity(10L)
                                .availableQuantity(0L)
                                .build()
                ))
                .build();

        when(usersRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(new Users()));
        when(inventoryServiceClient.checkProductsAvailability(any()))
                .thenReturn(inventoryResponse);

        OrderResponse response = orderService.processOrderCreation("user", orderRequest);

        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.FAILED);
        verify(kafkaProducerService, never()).sendSagaEvent(any());
    }

    @Test
    void handleInventoryReservedWhenOrderMapperReturnsNullThrowsException() {
        SagaState sagaState = new SagaState();
        sagaState.setUserId(1L);
        sagaState.setOrderId("order123");

        when(sagaStateRepository.findBySagaId(any()))
                .thenReturn(sagaState);
        when(orderMapper.buildFinalOrder(
                eq(1L),
                anyList(),
                eq("order123"))
        ).thenReturn(null);

        SagaEvent event = new SagaEvent();
        event.setSagaId("testSaga");
        event.setPayload(List.of(new ProductDto()));

        assertThatThrownBy(() -> orderService.handleInventoryReserved(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to build order");

        verify(kafkaProducerService, never()).sendSagaEvent(any());
    }

    @Test
    void handleInventoryReservedValidSagaStateSendsOrderCreateEvent() {
        String sagaId = "testSaga";
        String orderId = "order123";
        Long userId = 1L;

        SagaEvent event = new SagaEvent(
                sagaId,
                "inventory.reserved",
                SagaStatus.STARTED,
                List.of(new ProductDto()),
                null
        );

        SagaState sagaState = new SagaState();
        sagaState.setSagaId(sagaId);
        sagaState.setOrderId(orderId);
        sagaState.setUserId(userId);
        sagaState.setStatus(SagaStatus.STARTED);

        OrderDto expectedOrder = new OrderDto();
        expectedOrder.setOrderId(orderId);

        when(sagaStateRepository.findBySagaId(sagaId))
                .thenReturn(sagaState);

        when(orderMapper.buildFinalOrder(
                eq(userId),
                any(List.class),
                eq(orderId))
        ).thenReturn(expectedOrder);

        orderService.handleInventoryReserved(event);

        assertThat(sagaState.getStatus()).isEqualTo(SagaStatus.INVENTORY_RESERVED);
        verify(sagaStateRepository).save(sagaState);

        verify(kafkaProducerService).sendSagaEvent(argThat(e ->
                e.getEventType().equals("order.create") &&
                        e.getSagaId().equals(sagaId) &&
                        e.getPayload() == expectedOrder
        ));
    }

    @Test
    void handleSagaFailureWithReservedInventorySendsReleaseEvent() {
        String sagaId = "saga1";
        String orderId = "order123";
        String errorMessage = "Error message";

        SagaEvent event = new SagaEvent(
                sagaId,
                "inventory.reservation.failed",
                SagaStatus.STARTED,
                null,
                errorMessage
        );

        SagaState sagaState = new SagaState();
        sagaState.setSagaId(sagaId);
        sagaState.setOrderId(orderId);
        sagaState.setUserId(1L);
        sagaState.setStatus(SagaStatus.INVENTORY_RESERVED); // Ключевой статус!
        sagaState.setCreatedAt(LocalDateTime.now());
        sagaState.setUpdatedAt(LocalDateTime.now());

        when(sagaStateRepository.findBySagaId(sagaId))
                .thenReturn(sagaState);

        orderService.handleSagaFailure(event);

        assertThat(sagaState.getStatus()).isEqualTo(SagaStatus.COMPENSATING);

        verify(kafkaProducerService).sendSagaEvent(argThat(e ->
                e != null &&
                        e.getEventType().equals("inventory.release") &&
                        e.getSagaId().equals(sagaId) &&
                        e.getStatus() == SagaStatus.COMPENSATING &&
                        e.getPayload().equals(orderId) &&
                        e.getErrorMessage().equals(errorMessage)
        ));
    }

    @Test
    void getOrderStatusUserNotFoundThrowsException() {
        when(usersServiceImp.findUserByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderStatus("unknown", "order1"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getOrderStatusCompletedSagaReturnsCreatedStatus() {

        Users user = new Users();
        user.setId(1L);
        SagaState sagaState = new SagaState();
        sagaState.setStatus(SagaStatus.COMPLETED);

        when(usersServiceImp.findUserByUsername("user"))
                .thenReturn(Optional.of(user));
        when(sagaStateRepository.findByOrderIdAndUserId("order1", 1L))
                .thenReturn(sagaState);

        OrderStatusResponse response = orderService.getOrderStatus("user", "order1");

        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
    }
}
