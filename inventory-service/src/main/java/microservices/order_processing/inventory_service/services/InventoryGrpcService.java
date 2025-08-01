package microservices.order_processing.inventory_service.services;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import microservices.order_processing.inventory_service.grpc.AvailableProducts;
import microservices.order_processing.inventory_service.grpc.ProductsAvailabilityResponse;
import microservices.order_processing.inventory_service.grpc.ProductsRequest;
import microservices.order_processing.inventory_service.grpc.UnavailableProducts;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;

/**
 * Класс grpc-сервис, который проверяет наличие товаров, переданных в запросе на проверку.
 * Возвращается список доступных и недоступных товаров
 */
@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcService extends microservices.order_processing.inventory_service.grpc.
        OrderServiceGrpc.OrderServiceImplBase {

    private final ProductService productService;

/**
 * Проверяет доступность продуктов, запрошенных клиентом.
 * @param request gRPC запрос с перечнем продуктов
 * @param responseObserver объект для отправки ответа клиенту
 */
    @Override
    public void checkProductAvailability(ProductsRequest request,
                                         StreamObserver<ProductsAvailabilityResponse> responseObserver) {

        List<AvailableProducts> availabilityProducts =
                productService.getAvailabilityProducts(request.getProductsList());
        List<UnavailableProducts> unavailableProducts =
                productService.getUnavalabilityProducts(request.getProductsList());

        ProductsAvailabilityResponse response = ProductsAvailabilityResponse.newBuilder()
                .addAllAvailableProducts(availabilityProducts)
                .addAllUnavailableProducts(unavailableProducts)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
