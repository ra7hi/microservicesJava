package microservices.order_processing.order_service.services.components;

import microservices.order_processing.order_service.dto.OrderDto;
import microservices.order_processing.order_service.dto.ProductDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Компонент, отвечающий за построение объектов OrderDto из данных о продуктах и пользователе.
 * <p>
 * Используется для формирования итогового заказа с расчетом общей стоимости и установкой даты заказа.
 */
@Component
public class OrderMapper {

    /**
     * Создает DTO заказа на основе переданных параметров.
     * Общая цена заказа рассчитывается как сумма цен продуктов с учетом скидок.
     *
     * @param userId           идентификатор пользователя, оформившего заказ
     * @param availableProducts список доступных для заказа продуктов
     * @param orderId          уникальный идентификатор заказа
     * @return сформированный объект OrderDto с полной информацией о заказе
     */
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
