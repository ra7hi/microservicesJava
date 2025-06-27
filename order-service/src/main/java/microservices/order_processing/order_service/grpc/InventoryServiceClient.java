package microservices.order_processing.order_service.grpc;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import microservices.order_processing.order_service.dto.ProductDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceClient {

    private final OrderServiceGrpc.OrderServiceBlockingStub inventoryServiceStub;

    public List<ProductDto> checkProductsAvailability(List<Long> productIds) {
        try {
            ProductIdRequest request = ProductIdRequest.newBuilder()
                    .addAllProductIds(productIds)
                    .build();

            ProductsAvailabilityResponse response = inventoryServiceStub.checkProductAvailability(request);

            return response.getProductsList().stream().map(product ->
                    ProductDto.builder()
                            .productId(product.getProductId())
                            .productAvailability(product.getIsProductAvailability())
                            .price(product.getPrice())
                            .name(product.getName())
                            .sale(product.getSale())
                            .quantity(product.getQuantity())
                            .build()
                    ).collect(Collectors.toList());

        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Failed to check product availability: " + e.getMessage(), e);
        }
    }
}
