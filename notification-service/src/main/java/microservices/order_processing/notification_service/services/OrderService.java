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
import java.util.Optional;
import java.util.Set;

/**
 * Сервис создания заказа и чтения информации о них.
 * Создает заказ на основе переданного {@link OrderDto}
 * Предоставляет методы для чтения заказов (всех, по идентификатору заказа, по идентификатору пользователя)
 */
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDtoToOrderItemsDtoMapper orderDtoToOrderItemsDtoMapper;
    private final OrderItemsDtoToOrderItemsMapper orderItemsDtoToOrderItemsMapper;

    /**
     * Сохраняет заказ в БД, предварительно вызвав несколько преобразователей
     * @param dto DTO представления информации о заказе
     * @return сущность сохраненного заказа
     */
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

    /**
     * Возвращает все заказы
     * @return список сущностей Заказа
     */
    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    /**
     * Возвращает заказ по идентификатору
     * @param orderId идентификатор заказа
     * @return сущность найденного заказа
     */
    public Optional<Order> getOrderByOrderId(String orderId){
        return orderRepository.findOrderById(orderId);
    }

    /**
     * Возвращает все заказы пользователя
     * @param userId идентификатор пользователя
     * @return список сущностей Заказа пользователя
     */
    public List<Order> getAllOrdersByUserId(Long userId){
        return orderRepository.findOrdersByUserId(userId);
    }
}
