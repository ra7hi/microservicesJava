package microservices.order_processing.order_service.services.components;

import microservices.order_processing.order_service.dto.ProductDto;
import microservices.order_processing.order_service.saga.ProductReservation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductDtoToProductReservationMapper {
    public List<ProductReservation> mapToProductReservations(List<ProductDto> availableProducts) {
        return availableProducts.stream().map(productDto ->
                ProductReservation.builder()
                        .productId(productDto.getProductId())
                        .quantity(productDto.getQuantity())
                        .build()).collect(Collectors.toList());
    }
}
