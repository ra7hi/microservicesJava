package microservices.order_processing.notification_service.services.components;

import microservices.order_processing.notification_service.dto.OrderItemsDto;
import microservices.order_processing.notification_service.entities.OrderItems;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderItemsDtoToOrderItemsMapper {
    public Set<OrderItems> toOrderItems(Set<OrderItemsDto> orders) {
        return orders.stream().map(orderItemDto ->
                 OrderItems.builder()
                .productId(orderItemDto.getProductId())
                .quantity(orderItemDto.getQuantity())
                .price(orderItemDto.getPrice())
                .sale(orderItemDto.getSale())
                .build()
        ).collect(Collectors.toSet());
    }
}
