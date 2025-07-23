package microservices.order_processing.notification_service.services;

import lombok.RequiredArgsConstructor;
import microservices.order_processing.notification_service.dto.OrderDto;
import microservices.order_processing.notification_service.entities.Order;
import microservices.order_processing.notification_service.entities.OrderItems;
import microservices.order_processing.notification_service.repositories.OrderRepository;
import microservices.order_processing.notification_service.services.components.OrderDtoToOrderItemsDtoMapper;
import microservices.order_processing.notification_service.services.components.OrderItemsDtoToOrderItemsMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDtoToOrderItemsDtoMapper orderDtoToOrderItemsDtoMapper;
    private final OrderItemsDtoToOrderItemsMapper orderItemsDtoToOrderItemsMapper;

    public Order saveOrder(OrderDto dto) {
        Set<OrderItems> orderItems = orderItemsDtoToOrderItemsMapper.toOrderItems(
                orderDtoToOrderItemsDtoMapper.toOrderItemsDto(dto));

        Order order = Order.builder()
                .id(dto.getOrderId())
                .orderItems(orderItems)
                .totalPrice(dto.getTotalPrice())
                .userId(dto.getUserId())
                .orderDate(dto.getOrderDate())
                .build();

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public List<Order> getAllOrdersByOrderId(String orderId){
        return orderRepository.findOrdersById(orderId);
    }

    public List<Order> getAllOrdersByUserId(Long userId){
        return orderRepository.findOrdersByUserId(userId);
    }
}
