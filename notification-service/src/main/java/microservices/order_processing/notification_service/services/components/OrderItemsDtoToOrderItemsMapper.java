package microservices.order_processing.notification_service.services.components;

import microservices.order_processing.notification_service.dto.OrderItemsDto;
import microservices.order_processing.notification_service.entities.OrderItems;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Компонент формирования из DTO-класса {@link OrderItemsDto}, которое получаем после
 * формирования списка объектов состава заказа из {@link OrderDtoToOrderItemsDtoMapper}
 */
@Component
public class OrderItemsDtoToOrderItemsMapper {
    /**
     * Создает список сущностей элементов заказа из DTO представления состава заказа
     * @param orders DTO представления элементов заказа
     * @return список сущностей товаров
     */
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
