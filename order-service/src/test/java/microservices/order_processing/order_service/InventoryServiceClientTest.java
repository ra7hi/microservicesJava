package microservices.order_processing.order_service;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import microservices.order_processing.order_service.controllers.requests.OrderRequest;
import microservices.order_processing.order_service.controllers.requests.ProductOrderRequest;
import microservices.order_processing.order_service.controllers.responses.OrderResponse;
import microservices.order_processing.order_service.enums.OrderStatus;
import microservices.order_processing.order_service.dto.ProductDto;
import microservices.order_processing.order_service.dto.UnavailableProductDto;
import microservices.order_processing.order_service.grpc.InventoryServiceClient;
import microservices.order_processing.order_service.grpc.Product;
import microservices.order_processing.order_service.grpc.ProductsAvailabilityResponse;
import microservices.order_processing.order_service.grpc.ProductsRequest;
import microservices.order_processing.order_service.grpc.components.InventoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;


class InventoryServiceClientTest {

    @Mock
    private microservices.order_processing.order_service.grpc.OrderServiceGrpc.OrderServiceBlockingStub inventoryServiceStub;

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryServiceClient inventoryServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkProductsAvailability_success() {
        ProductOrderRequest productRequest1 = new ProductOrderRequest(1L, 2L);
        ProductOrderRequest productRequest2 = new ProductOrderRequest(2L, 3L);
        List<ProductOrderRequest> productRequests = Arrays.asList(productRequest1, productRequest2);
        OrderRequest orderRequest = new OrderRequest(productRequests, "order123");

        ProductsRequest grpcRequest = ProductsRequest.newBuilder()
                .addProducts(Product.newBuilder().setProductId(1L).setQuantity(2L).build())
                .addProducts(Product.newBuilder().setProductId(2L).setQuantity(3L).build())
                .build();

        ProductsAvailabilityResponse grpcResponse = ProductsAvailabilityResponse.newBuilder().build();

        OrderResponse expectedResponse = OrderResponse.builder()
                .orderStatus(OrderStatus.CREATED)
                .availableProducts(List.of(
                        new ProductDto(1L, "product1", 100.0, 10L, 0.05)))
                .unavailableProducts(List.of(
                        UnavailableProductDto.builder()
                                .productId(2L)
                                .reason("Out of stock")
                                .requestedQuantity(3L)
                                .availableQuantity(0L)
                                .build()))
                .build();


        when(inventoryServiceStub.checkProductAvailability(grpcRequest)).thenReturn(grpcResponse);
        when(inventoryMapper.buildFinalOrder(grpcResponse)).thenReturn(expectedResponse);

        OrderResponse actualResponse = inventoryServiceClient.checkProductsAvailability(orderRequest);

        assertEquals(expectedResponse, actualResponse);
        verify(inventoryServiceStub).checkProductAvailability(grpcRequest);
        verify(inventoryMapper).buildFinalOrder(grpcResponse);
    }

    @Test
    void checkProductsAvailability_grpcError() {
        ProductOrderRequest productRequest = new ProductOrderRequest(1L, 2L);
        OrderRequest orderRequest = new OrderRequest(List.of(productRequest), "order123");

        ProductsRequest grpcRequest = ProductsRequest.newBuilder()
                .addProducts(Product.newBuilder().setProductId(1L).setQuantity(2).build())
                .build();

        StatusRuntimeException grpcException = new StatusRuntimeException(Status.INTERNAL);

        when(inventoryServiceStub.checkProductAvailability(grpcRequest)).thenThrow(grpcException);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                inventoryServiceClient.checkProductsAvailability(orderRequest));

        assertTrue(exception.getMessage().contains("Failed to check product availability"));
        verify(inventoryServiceStub).checkProductAvailability(grpcRequest);
        verifyNoInteractions(inventoryMapper);
    }
}
