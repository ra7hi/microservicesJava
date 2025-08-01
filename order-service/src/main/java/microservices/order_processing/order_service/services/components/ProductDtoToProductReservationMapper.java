package microservices.order_processing.order_service.services.components;

import microservices.order_processing.order_service.dto.ProductDto;
import microservices.order_processing.order_service.saga.ProductReservation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Компонент для преобразования списка DTO продуктов {@link ProductDto}
 * в список объектов резервации продуктов {@link ProductReservation}.
 * <p>
 * Используется для передачи информации о резервируемых продуктах в сагу.
 */
@Component
public class ProductDtoToProductReservationMapper {

    /**
     * Преобразует список доступных продуктов в список объектов резервирования,
     * содержащих идентификатор продукта и количество для резервации.
     *
     * @param availableProducts список доступных продуктов {@link ProductDto}
     * @return список объектов резервирования продуктов {@link ProductReservation}
     */
    public List<ProductReservation> mapToProductReservations(List<ProductDto> availableProducts) {
        return availableProducts.stream().map(productDto ->
                ProductReservation.builder()
                        .productId(productDto.getProductId())
                        .quantity(productDto.getQuantity())
                        .build()).collect(Collectors.toList());
    }
}
