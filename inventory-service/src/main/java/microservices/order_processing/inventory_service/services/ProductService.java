package microservices.order_processing.inventory_service.services;

import lombok.RequiredArgsConstructor;
import microservices.order_processing.inventory_service.enums.UnavalibleProductReasons;
import microservices.order_processing.inventory_service.dto.ProductDto;
import microservices.order_processing.inventory_service.entities.ProductEntity;
import microservices.order_processing.inventory_service.exceptions.ProductNotFoundException;
import microservices.order_processing.inventory_service.repositories.ProductRepository;
import microservices.order_processing.inventory_service.grpc.AvailableProducts;
import microservices.order_processing.inventory_service.grpc.Product;
import microservices.order_processing.inventory_service.grpc.UnavailableProducts;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления товарами.
 * Реализует CRUD операции и методы для проверки доступности товаров.
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductDto findProductById(long id) {
        ProductDto productDto = new ProductDto();
        Optional<ProductEntity> product = productRepository.findById(id);
        if (product.isPresent()) {
            BeanUtils.copyProperties(product.get(), productDto);
        }
        else{
            throw new ProductNotFoundException("Product not found!");
        }
        return productDto;
    }

    public List<ProductDto> findAllProducts() {
        List<ProductDto> productDtos = new ArrayList<>();
        productRepository.findAll().forEach(productEntity -> productDtos.add(findProductById(productEntity.getId())));
        return productDtos;
    }

    public void createProduct (ProductDto productDto) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productDto, productEntity);
        productRepository.save(productEntity);
    }

    public void updateProduct (ProductDto productDto, Long product_id) {
        if(productRepository.findById(product_id).isPresent()) {
            ProductEntity productEntity = new ProductEntity();
            BeanUtils.copyProperties(productDto, productEntity);
            productRepository.save(productEntity);
        }
        else{
            throw new ProductNotFoundException("Product not found!");
        }
    }

    public void deleteProduct (Long product_id) {
        if(productRepository.existsById(product_id)) {
            productRepository.deleteById(product_id);
        }
        else {
            throw new ProductNotFoundException("Product not found!");
        }
    }

    /**
     * Получает список доступных продуктов из переданного списка.
     *
     * @param productList список продуктов для проверки
     * @return список доступных продуктов с деталями {@link AvailableProducts}
     */
    public List<microservices.order_processing.inventory_service.grpc.AvailableProducts> getAvailabilityProducts(List<Product> productList) {
        List<AvailableProducts> availabilities = new ArrayList<>();

        for (Product product : productList) {
            Optional<ProductEntity> productEntity = productRepository.findProductById(product.getProductId());
            if(productEntity.isPresent()){
                if(productEntity.get().getAvailableQuantity() >= product.getQuantity()){
                    availabilities.add(toProductAvailability(productEntity.get()));
                }
            }
        }

        return availabilities;
    }

    /**
     * Получает список недоступных продуктов из переданного списка
     *
     * @param productList список продуктов для проверки
     * @return список недоступных продуктов с причиной {@link UnavailableProducts}
     */
    public List<UnavailableProducts> getUnavalabilityProducts(List<Product> productList) {
        List<UnavailableProducts> unavailabilities = new ArrayList<>();
        for (Product product : productList) {
            Optional<ProductEntity> productEntity = productRepository.findProductById(product.getProductId());
            if(productEntity.isEmpty()){
                unavailabilities.add(toProductsUnavalability(product.getProductId(),
                        UnavalibleProductReasons.NOT_FOUND.toString(),
                        product.getQuantity(), 0L));
            } else if (productEntity.get().getAvailableQuantity() < product.getQuantity()) {
                unavailabilities.add(toProductsUnavalability(product.getProductId(),
                        UnavalibleProductReasons.INSUFFICIENT_QUANTITY.toString(),
                        product.getQuantity(), productEntity.get().getAvailableQuantity()));
            }
        }
        return unavailabilities;
    }

    /**
     * Создает объект UnavailableProducts с указанием причины отсутствия
     * @param id идентификатор продукта
     * @param reason причина
     * @param requestedQuantity запрашиваемое количество продукта
     * @param availableQuantity фактически доступное количество продукта
     * @return {@link UnavailableProducts}
     */
    private UnavailableProducts toProductsUnavalability(Long id, String reason, Long requestedQuantity, Long availableQuantity) {
        return UnavailableProducts.newBuilder()
                .setProductId(id)
                .setReason(reason)
                .setRequestedQuantity(requestedQuantity)
                .setAvailableQuantity(availableQuantity)
                .build();
    }

    /**
     * Преобразует сущность продукта в объект доступного продукта с деталями
     * @param productEntity
     * @return {@link AvailableProducts}
     */
    private AvailableProducts toProductAvailability(ProductEntity productEntity) {
        return AvailableProducts.newBuilder()
                .setProductId(productEntity.getId())
                .setName(productEntity.getName())
                .setQuantity(productEntity.getAvailableQuantity())
                .setPrice(productEntity.getPrice())
                .setSale(productEntity.getSale())
                .build();
    }
}
