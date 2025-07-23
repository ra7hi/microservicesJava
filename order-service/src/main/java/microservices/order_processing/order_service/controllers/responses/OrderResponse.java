package microservices.order_processing.order_service.controllers.responses;

import lombok.Builder;
import lombok.Data;
import microservices.order_processing.order_service.dto.ProductDto;
import microservices.order_processing.order_service.dto.UnavailableProductDto;

import java.util.List;

@Builder
@Data
public class OrderResponse {
    boolean isCreared;
    List<ProductDto> availableProducts;
    List<UnavailableProductDto> unavailableProducts;
}
