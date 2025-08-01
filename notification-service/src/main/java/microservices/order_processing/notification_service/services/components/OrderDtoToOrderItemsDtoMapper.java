package microservices.order_processing.notification_service.services.components;

import microservices.order_processing.notification_service.dto.OrderDto;
import microservices.order_processing.notification_service.dto.OrderItemsDto;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Компонент формирования из DTO-класса {@link OrderDto}, которое получаем из саги
 * в событии order.create список объектов состава заказа {@link OrderItemsDto}
 */
@Component
public class OrderDtoToOrderItemsDtoMapper {

    /**
     * Создает список элементов заказа на основе переданного DTO-заказа
     * Из заказа получает список продуктов и формирует список элементов заказа
     * @param order DTO представления информации о заказе
     * @return список товаров в составе заказа OrderItemsDto
     */
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
