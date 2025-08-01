package microservices.order_processing.order_service.grpc;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import microservices.order_processing.order_service.controllers.requests.OrderRequest;
import microservices.order_processing.order_service.controllers.responses.OrderResponse;
import microservices.order_processing.order_service.grpc.components.InventoryMapper;
import org.springframework.stereotype.Service;
import microservices.order_processing.order_service.controllers.requests.ProductOrderRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * gRPC-клиент для взаимодействия с Inventory Service.
 * Выполняет проверку доступности товаров и преобразует результат в формат для REST-ответа.
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceClient {

    private final OrderServiceGrpc.OrderServiceBlockingStub inventoryServiceStub;
    private final InventoryMapper inventoryMapper;

    /**
     * Проверяет доступность товаров, запрошенных в заказе, через gRPC Inventory Service.
     *
     * @param orderRequest запрос на оформление заказа, содержащий список продуктов
     * @return {@link OrderResponse} с доступными и недоступными товарами
     * @throws RuntimeException если gRPC-вызов завершился с ошибкой
     */
    public OrderResponse checkProductsAvailability(OrderRequest orderRequest) {
        List<Product> productList = new ArrayList<>();
        for(ProductOrderRequest productOrderRequest : orderRequest.getProductsRequest()){
            Product product = Product.newBuilder().setProductId(productOrderRequest.getProductId())
                    .setQuantity(productOrderRequest.getQuantity()).build();
            productList.add(product);
        }
        try {
            ProductsRequest request = ProductsRequest.newBuilder()
                    .addAllProducts(productList)
                    .build();

            ProductsAvailabilityResponse response = inventoryServiceStub.checkProductAvailability(request);
            return inventoryMapper.buildFinalOrder(response);

        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Failed to check product availability: " + e.getMessage(), e);
        }
    }
}
