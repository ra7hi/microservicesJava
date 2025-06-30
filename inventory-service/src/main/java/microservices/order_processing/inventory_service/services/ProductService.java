package microservices.order_processing.inventory_service.services;

import microservices.order_processing.inventory_service.dto.ProductDto;
import microservices.order_processing.inventory_service.entities.Product;
import microservices.order_processing.inventory_service.exceptions.ProductNotFoundException;
import microservices.order_processing.inventory_service.grpc.ProductAvailability;
import microservices.order_processing.inventory_service.repositories.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDto findProductById(long id) {
        ProductDto productDto = new ProductDto();
        Optional<Product> product = productRepository.findById(id);
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
        productRepository.findAll().forEach(product -> productDtos.add(findProductById(product.getId())));
        return productDtos;
    }

    public void createProduct (ProductDto productDto) {
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        productRepository.save(product);
    }

    public void updateProduct (ProductDto productDto, Long product_id) {
        if(productRepository.findById(product_id).isPresent()) {
            Product product = new Product();
            BeanUtils.copyProperties(productDto, product);
            productRepository.save(product);
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

    public List<ProductAvailability> getProductsAvailability(List<Long> productIds) {
        List<ProductAvailability> availabilities = new ArrayList<>();

        for (Long productId : productIds) {
            Optional<Product> product = productRepository.findProductById(productId);
            product.ifPresent(p -> availabilities.add(toProductAvailability(p)));
        }

        return availabilities;
    }

    private ProductAvailability toProductAvailability(Product product) {
        return ProductAvailability.newBuilder()
                .setProductId(product.getId())
                .setIsProductAvailability(product.getQuantity() > 0)
                .setName(product.getName())
                .setQuantity(product.getQuantity())
                .setPrice(product.getPrice())
                .setSale(product.getSale())
                .build();
    }
}
