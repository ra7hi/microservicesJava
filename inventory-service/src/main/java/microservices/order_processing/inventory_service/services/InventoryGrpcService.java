package microservices.order_processing.inventory_service.services;

import io.grpc.stub.StreamObserver;
import microservices.order_processing.inventory_service.grpc.ProductAvailability;
import microservices.order_processing.inventory_service.grpc.ProductIdRequest;
import microservices.order_processing.inventory_service.grpc.ProductsAvailabilityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class InventoryGrpcService extends microservices.order_processing.inventory_service.grpc.
        OrderServiceGrpc.OrderServiceImplBase {

    private final ProductService productService;

    @Autowired
    public InventoryGrpcService(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void checkProductAvailability(ProductIdRequest request,
                                         StreamObserver<ProductsAvailabilityResponse> responseObserver) {

        List<ProductAvailability> productAvailabilities =
                productService.getProductsAvailability(request.getProductIdsList());

        ProductsAvailabilityResponse response = ProductsAvailabilityResponse.newBuilder()
                .addAllProducts(productAvailabilities)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
