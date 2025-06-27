package microservices.order_processing.order_service.services;

import microservices.order_processing.order_service.dto.OrderDto;
import microservices.order_processing.order_service.dto.ProductDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    public OrderDto createFinalOrder(Long userId, List<ProductDto> availableProducts) {

        double totalPrice = availableProducts.stream()
                .mapToDouble(p -> p.getPrice() - p.getPrice() * p.getSale())
                .sum();

       return OrderDto.builder()
                .orderId(UUID.randomUUID().toString())
                .userId(userId)
                .products(availableProducts)
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .build();
    }
}
