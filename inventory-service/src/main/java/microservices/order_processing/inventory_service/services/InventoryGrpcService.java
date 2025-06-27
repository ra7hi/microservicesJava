package microservices.order_processing.inventory_service.services;

import io.grpc.stub.StreamObserver;
import microservices.order_processing.inventory_service.entities.Product;
import microservices.order_processing.inventory_service.grpc.ProductAvailability;
import microservices.order_processing.inventory_service.grpc.ProductIdRequest;
import microservices.order_processing.inventory_service.grpc.ProductsAvailabilityResponse;
import microservices.order_processing.inventory_service.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@GrpcService
public class InventoryGrpcService extends microservices.order_processing.inventory_service.grpc.
        OrderServiceGrpc.OrderServiceImplBase {

    private final ProductRepository productRepository;

    @Autowired
    public InventoryGrpcService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void checkProductAvailability(ProductIdRequest request,
                                         StreamObserver<ProductsAvailabilityResponse> responseObserver) {

        List<ProductAvailability> productAvailabilities = new ArrayList<>();

        for(Long productId : request.getProductIdsList()) {
            Optional<Product> product = productRepository.findProductById(productId);
            if(product.isPresent()) {
                Product responseProduct = product.get();
                productAvailabilities.add(
                        ProductAvailability.newBuilder()
                                .setProductId(responseProduct.getId())
                                .setIsProductAvailability(responseProduct.getQuantity() > 0)
                                .setName(responseProduct.getName())
                                .setQuantity(responseProduct.getQuantity())
                                .setPrice(responseProduct.getPrice())
                                .setSale(responseProduct.getSale())
                                .build()
                );
            }
        }

        ProductsAvailabilityResponse response = ProductsAvailabilityResponse.newBuilder()
                .addAllProducts(productAvailabilities)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
