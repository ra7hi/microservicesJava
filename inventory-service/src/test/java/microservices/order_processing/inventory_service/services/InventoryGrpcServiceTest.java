package microservices.order_processing.inventory_service.services;

import io.grpc.stub.StreamObserver;
import microservices.order_processing.inventory_service.grpc.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class InventoryGrpcServiceTest {

    private ProductService productService;
    private InventoryGrpcService inventoryGrpcService;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        inventoryGrpcService = new InventoryGrpcService(productService);
    }

    @Test
    void testCheckProductAvailability() {
        Product product = Product.newBuilder()
                .setProductId(1L)
                .setQuantity(2L)
                .build();

        ProductsRequest request = ProductsRequest.newBuilder()
                .addProducts(product)
                .build();

        AvailableProducts availableProduct = AvailableProducts.newBuilder()
                .setProductId(1L)
                .setName("TestProduct")
                .setQuantity(5L)
                .setPrice(10.0)
                .setSale(0.0)
                .build();

        when(productService.getAvailabilityProducts(anyList()))
                .thenReturn(List.of(availableProduct));

        when(productService.getUnavalabilityProducts(anyList()))
                .thenReturn(Collections.emptyList());

        @SuppressWarnings("unchecked")
        StreamObserver<ProductsAvailabilityResponse> responseObserver = mock(StreamObserver.class);

        inventoryGrpcService.checkProductAvailability(request, responseObserver);

        ArgumentCaptor<ProductsAvailabilityResponse> captor = ArgumentCaptor.forClass(ProductsAvailabilityResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        ProductsAvailabilityResponse response = captor.getValue();
        assertEquals(1, response.getAvailableProductsCount());
        assertEquals(0, response.getUnavailableProductsCount());

        AvailableProducts returnedProduct = response.getAvailableProducts(0);
        assertEquals(1L, returnedProduct.getProductId());
        assertEquals("TestProduct", returnedProduct.getName());
        assertEquals(5L, returnedProduct.getQuantity());
        assertEquals(10.0, returnedProduct.getPrice());
    }

    @Test
    void testCheckProductAvailabilityUnavailableProduct() {
        Product product = Product.newBuilder()
                .setProductId(2L)
                .setQuantity(10L)
                .build();

        ProductsRequest request = ProductsRequest.newBuilder()
                .addProducts(product)
                .build();

        UnavailableProducts unavailableProduct = UnavailableProducts.newBuilder()
                .setProductId(2L)
                .setReason("INSUFFICIENT_QUANTITY")
                .setRequestedQuantity(10L)
                .setAvailableQuantity(5L)
                .build();

        when(productService.getAvailabilityProducts(anyList()))
                .thenReturn(Collections.emptyList());

        when(productService.getUnavalabilityProducts(anyList()))
                .thenReturn(List.of(unavailableProduct));

        @SuppressWarnings("unchecked")
        StreamObserver<ProductsAvailabilityResponse> responseObserver = mock(StreamObserver.class);

        inventoryGrpcService.checkProductAvailability(request, responseObserver);

        ArgumentCaptor<ProductsAvailabilityResponse> captor = ArgumentCaptor.forClass(ProductsAvailabilityResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        ProductsAvailabilityResponse response = captor.getValue();
        assertEquals(0, response.getAvailableProductsCount());
        assertEquals(1, response.getUnavailableProductsCount());

        UnavailableProducts returnedUnavailable = response.getUnavailableProducts(0);
        assertEquals(2L, returnedUnavailable.getProductId());
        assertEquals("INSUFFICIENT_QUANTITY", returnedUnavailable.getReason());
        assertEquals(10L, returnedUnavailable.getRequestedQuantity());
        assertEquals(5L, returnedUnavailable.getAvailableQuantity());
    }
}
