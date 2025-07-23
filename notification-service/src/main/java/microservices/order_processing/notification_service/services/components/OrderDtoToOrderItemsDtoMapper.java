package microservices.order_processing.notification_service.services.components;

import microservices.order_processing.notification_service.dto.OrderDto;
import microservices.order_processing.notification_service.dto.OrderItemsDto;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderDtoToOrderItemsDtoMapper {
    public Set<OrderItemsDto> toOrderItemsDto(OrderDto order) {
        return order.getProducts().stream()
                .map(productDto ->
                        OrderItemsDto.builder()
                                .productId(productDto.getProductId())
                                .quantity(productDto.getQuantity())
                                .price(productDto.getPrice())
                                .sale(productDto.getSale())
                                .build())
                .collect(Collectors.toSet());
    }
}
