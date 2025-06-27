package microservices.order_processing.notification_service.OrderService;

import microservices.order_processing.notification_service.dto.OrderDto;
import microservices.order_processing.notification_service.entities.Order;
import microservices.order_processing.notification_service.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void saveOrder(OrderDto dto) {
        List<Order> orders = dto.getProducts().stream()
                .map(product -> Order.builder()
                        .orderId(dto.getOrderId())
                        .productId(product.getProductId())
                        .quantity(product.getQuantity())
                        .price(product.getPrice())
                        .sale(product.getSale())
                        .totalPrice(dto.getTotalPrice())
                        .userId(dto.getUserId())
                        .orderDate(dto.getOrderDate())
                        .build())
                .collect(Collectors.toList());

        orderRepository.saveAll(orders);
    }

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public List<Order> getAllOrdersByOrderId(Long orderId){
        return orderRepository.findOrdersByOrderId(orderId);
    }

    public List<Order> getAllOrdersByUserId(Long userId){
        return orderRepository.findOrdersByUserId(userId);
    }
}
