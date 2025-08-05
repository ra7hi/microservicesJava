package microservices.order_processing.notification_service.services;

import microservices.order_processing.notification_service.dto.OrderDto;
import microservices.order_processing.notification_service.entities.Order;
import microservices.order_processing.notification_service.entities.OrderItems;
import microservices.order_processing.notification_service.repositories.OrderRepository;
import microservices.order_processing.notification_service.services.components.OrderDtoToOrderItemsDtoMapper;
import microservices.order_processing.notification_service.services.components.OrderItemsDtoToOrderItemsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDtoToOrderItemsDtoMapper orderDtoToOrderItemsDtoMapper;

    @Mock
    private OrderItemsDtoToOrderItemsMapper orderItemsDtoToOrderItemsMapper;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveOrderShouldMapAndSaveOrder() {
        OrderDto orderDto = OrderDto.builder()
                .orderId("order123")
                .userId(42L)
                .totalPrice(123.45)
                .orderDate(LocalDateTime.now())
                .build();

        Set<OrderItems> orderItemsSet = Set.of(OrderItems.builder().build());
        when(orderDtoToOrderItemsDtoMapper.toOrderItemsDto(orderDto)).thenReturn(Set.of());
        when(orderItemsDtoToOrderItemsMapper.toOrderItems(any())).thenReturn(orderItemsSet);

        Order savedOrder = Order.builder()
                .id(orderDto.getOrderId())
                .orderItems(orderItemsSet)
                .totalPrice(orderDto.getTotalPrice())
                .userId(orderDto.getUserId())
                .orderDate(orderDto.getOrderDate())
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.saveOrder(orderDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderDto.getOrderId());
        assertThat(result.getUserId()).isEqualTo(orderDto.getUserId());
        assertThat(result.getTotalPrice()).isEqualTo(orderDto.getTotalPrice());
        assertThat(result.getOrderItems()).isEqualTo(orderItemsSet);

        verify(orderDtoToOrderItemsDtoMapper).toOrderItemsDto(orderDto);
        verify(orderItemsDtoToOrderItemsMapper).toOrderItems(any());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getAllOrdersShouldReturnAllOrdersFromRepository() {
        List<Order> orders = List.of(Order.builder().id("1").build(), Order.builder().id("2").build());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertThat(result).isEqualTo(orders);
        verify(orderRepository).findAll();
    }

    @Test
    void getOrderByOrderIdShouldReturnOrderIfExists() {
        String orderId = "order-xyz";
        Order order = Order.builder().id(orderId).build();
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderByOrderId(orderId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(order);
        verify(orderRepository).findOrderById(orderId);
    }

    @Test
    void getOrderByOrderIdShouldReturnEmptyIfNotFound() {
        String orderId = "non-existing";
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.getOrderByOrderId(orderId);

        assertThat(result).isEmpty();
        verify(orderRepository).findOrderById(orderId);
    }

    @Test
    void getAllOrdersByUserIdShouldReturnOrdersForUser() {
        Long userId = 10L;
        List<Order> orders = List.of(Order.builder().userId(userId).build());
        when(orderRepository.findOrdersByUserId(userId)).thenReturn(orders);

        List<Order> result = orderService.getAllOrdersByUserId(userId);

        assertThat(result).isEqualTo(orders);
        verify(orderRepository).findOrdersByUserId(userId);
    }
}
