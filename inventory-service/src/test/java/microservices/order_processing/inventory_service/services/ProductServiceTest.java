package microservices.order_processing.inventory_service.services;

import microservices.order_processing.inventory_service.dto.ProductDto;
import microservices.order_processing.inventory_service.entities.ProductEntity;
import microservices.order_processing.inventory_service.enums.UnavalibleProductReasons;
import microservices.order_processing.inventory_service.exceptions.ProductNotFoundException;
import microservices.order_processing.inventory_service.grpc.Product;
import microservices.order_processing.inventory_service.repositories.ProductRepository;
import microservices.order_processing.inventory_service.grpc.AvailableProducts;
import microservices.order_processing.inventory_service.grpc.UnavailableProducts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindProductById_success() {
        ProductEntity entity = createProductEntity(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));

        ProductDto result = productService.findProductById(1L);

        assertEquals(entity.getName(), result.getName());
        assertEquals(entity.getPrice(), result.getPrice());
        assertEquals(entity.getAvailableQuantity(), result.getAvailableQuantity());
        assertEquals(entity.getSale(), result.getSale());
    }

    @Test
    void testFindProductById_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findProductById(1L));
    }

    @Test
    void testFindAllProducts() {
        ProductEntity entity1 = createProductEntity(1L);
        ProductEntity entity2 = createProductEntity(2L);

        when(productRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(productRepository.findById(1L)).thenReturn(Optional.of(entity1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(entity2));

        List<ProductDto> result = productService.findAllProducts();

        assertEquals(2, result.size());
    }

    @Test
    void testCreateProduct() {
        ProductDto dto = createProductDto();

        productService.createProduct(dto);

        verify(productRepository, times(1)).save(any(ProductEntity.class));
    }

    @Test
    void testUpdateProduct_success() {
        ProductDto dto = createProductDto();
        when(productRepository.findById(1L)).thenReturn(Optional.of(createProductEntity(1L)));

        productService.updateProduct(dto, 1L);

        verify(productRepository, times(1)).save(any(ProductEntity.class));
    }

    @Test
    void testUpdateProduct_notFound() {
        ProductDto dto = createProductDto();
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(dto, 1L));
    }

    @Test
    void testDeleteProduct_success() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProduct_notFound() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void testGetAvailabilityProducts() {
        ProductEntity entity = createProductEntity(1L);
        when(productRepository.findProductById(1L)).thenReturn(Optional.of(entity));

        Product grpcProduct = Product.newBuilder()
                .setProductId(1L)
                .setQuantity(5L)
                .build();

        List<AvailableProducts> result = productService.getAvailabilityProducts(List.of(grpcProduct));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getProductId());
    }

    @Test
    void testGetUnavalabilityProducts_notFound() {
        when(productRepository.findProductById(1L)).thenReturn(Optional.empty());

        Product grpcProduct = Product.newBuilder()
                .setProductId(1L)
                .setQuantity(5L)
                .build();

        List<UnavailableProducts> result = productService.getUnavalabilityProducts(List.of(grpcProduct));

        assertEquals(1, result.size());
        assertEquals(UnavalibleProductReasons.NOT_FOUND.toString(), result.get(0).getReason());
    }

    @Test
    void testGetUnavalabilityProducts_insufficientQuantity() {
        ProductEntity entity = createProductEntity(1L);
        entity.setAvailableQuantity(2L);
        when(productRepository.findProductById(1L)).thenReturn(Optional.of(entity));

        Product grpcProduct = Product.newBuilder()
                .setProductId(1L)
                .setQuantity(5L)
                .build();

        List<UnavailableProducts> result = productService.getUnavalabilityProducts(List.of(grpcProduct));

        assertEquals(1, result.size());
        assertEquals(UnavalibleProductReasons.INSUFFICIENT_QUANTITY.toString(), result.getFirst().getReason());
    }

    private ProductDto createProductDto() {
        return ProductDto.builder()
                .name("Test Product")
                .price(10.0)
                .availableQuantity(5L)
                .sale(0.1)
                .build();
    }

    private ProductEntity createProductEntity(Long id) {
        ProductEntity entity = new ProductEntity();
        entity.setId(id);
        entity.setName("Test Product");
        entity.setPrice(10.0);
        entity.setAvailableQuantity(10L);
        entity.setSale(0.1);
        return entity;
    }
}
