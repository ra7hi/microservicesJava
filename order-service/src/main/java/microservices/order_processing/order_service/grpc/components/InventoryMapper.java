package microservices.order_processing.order_service.grpc.components;

import microservices.order_processing.order_service.controllers.responses.OrderResponse;
import microservices.order_processing.order_service.dto.ProductDto;
import microservices.order_processing.order_service.dto.UnavailableProductDto;
import microservices.order_processing.order_service.grpc.ProductsAvailabilityResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryMapper {
    public OrderResponse buildFinalOrder(ProductsAvailabilityResponse productsAvailabilityResponse) {
        List<ProductDto> productDtos = productsAvailabilityResponse.getAvailableProductsList().stream().map(product ->
                ProductDto.builder()
                        .productId(product.getProductId())
                        .price(product.getPrice())
                        .name(product.getName())
                        .sale(product.getSale())
                        .quantity(product.getQuantity())
                        .build()
        ).collect(Collectors.toList());

        List<UnavailableProductDto> unavailableProductDtos = productsAvailabilityResponse.getUnavailableProductsList()
                .stream().map(unavailableProduct ->
                        UnavailableProductDto.builder()
                                .productId(unavailableProduct.getProductId())
                                .reason(unavailableProduct.getReason())
                                .requestedQuantity(unavailableProduct.getRequestedQuantity())
                                .availableQuantity(unavailableProduct.getAvailableQuantity())
                                .build())
                .collect(Collectors.toList());
        return OrderResponse.builder()
                .isCreared(unavailableProductDtos.isEmpty())
                .availableProducts(productDtos)
                .unavailableProducts(unavailableProductDtos)
                .build();
    }
}
