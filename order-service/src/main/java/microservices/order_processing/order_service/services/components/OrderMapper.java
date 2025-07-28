package microservices.order_processing.order_service.services.components;

import microservices.order_processing.order_service.dto.OrderDto;
import microservices.order_processing.order_service.dto.ProductDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderMapper {
    public OrderDto buildFinalOrder(Long userId, List<ProductDto> availableProducts, String orderId) {
        double totalPrice = availableProducts.stream()
                .mapToDouble(p -> p.getPrice() - p.getPrice() * p.getSale())
                .sum();

        return OrderDto.builder()
                .orderId(orderId)
                .userId(userId)
                .products(availableProducts)
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .build();
    }
}
