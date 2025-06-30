package microservices.order_processing.inventory_service.controllers;

import microservices.order_processing.inventory_service.dto.ProductDto;
import microservices.order_processing.inventory_service.entities.Product;
import microservices.order_processing.inventory_service.services.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getProducts(){
        return ResponseEntity.ok().body(productService.findAllProducts());
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<?> getProductById(@PathVariable Long product_id) {
        return ResponseEntity.ok().body(productService.findProductById(product_id));
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody @Validated ProductDto productDto) {
        productService.createProduct(productDto);
        return ResponseEntity.ok("Product created");
    }

    @PatchMapping("/{product_id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long product_id, @RequestBody @Validated ProductDto productDto) {
        productService.updateProduct(productDto, product_id);
        return ResponseEntity.ok("Product updated");
    }

    @DeleteMapping("/{product_id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long product_id) {
        productService.deleteProduct(product_id);
        return ResponseEntity.ok("Product deleted");
    }
}
