package microservices.order_processing.inventory_service.controllers;

import microservices.order_processing.inventory_service.dto.ProductDto;
import microservices.order_processing.inventory_service.entities.Product;
import microservices.order_processing.inventory_service.repositories.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<?> getProducts(){
        return ResponseEntity.ok().body(productRepository.findAll());
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<?> getProductById(@PathVariable Long product_id) {
        return ResponseEntity.ok().body(productRepository.findById(product_id));
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody @Validated ProductDto productDto) {
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        productRepository.save(product);
        return ResponseEntity.ok("Product created");
    }

    @PatchMapping("/{product_id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long product_id, @RequestBody @Validated ProductDto productDto) {
        if(productRepository.findById(product_id).isPresent()) {
            Product product = new Product();
            BeanUtils.copyProperties(productDto, product);
            productRepository.save(product);
            return ResponseEntity.ok("Product updated");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }

    @DeleteMapping("/{product_id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long product_id) {
        if(productRepository.existsById(product_id)) {
            productRepository.deleteById(product_id);
            return ResponseEntity.ok("Product deleted");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }
}
