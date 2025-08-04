package microservices.order_processing.inventory_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import microservices.order_processing.inventory_service.dto.ProductDto;
import microservices.order_processing.inventory_service.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Продукты", description = "Операции с продуктами в сервисе инвентаризации")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Получить список всех продуктов.
     *
     * @return список продуктов
     */
    @Operation(summary = "Получить все продукты", description = "Возвращает список всех продуктов в инвентаре.")
    @ApiResponse(responseCode = "200", description = "Продукты успешно получены")
    @GetMapping
    public ResponseEntity<?> getProducts() {
        return ResponseEntity.ok().body(productService.findAllProducts());
    }

    /**
     * Получить продукт по его ID.
     *
     * @param product_id идентификатор продукта
     * @return информация о продукте
     */
    @Operation(summary = "Получить продукт по ID", description = "Возвращает информацию о продукте по заданному ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт найден"),
            @ApiResponse(responseCode = "404", description = "Продукт не найден")
    })
    @GetMapping("/{product_id}")
    public ResponseEntity<?> getProductById(
            @Parameter(description = "ID продукта", example = "1")
            @PathVariable Long product_id) {
        return ResponseEntity.ok().body(productService.findProductById(product_id));
    }

    /**
     * Создать новый продукт.
     *
     * @param productDto данные продукта
     * @return сообщение об успехе
     */
    @Operation(summary = "Создать продукт", description = "Добавляет новый продукт в инвентарь.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    @PostMapping
    public ResponseEntity<?> createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные нового продукта", required = true)
            @RequestBody @Validated ProductDto productDto) {
        productService.createProduct(productDto);
        return ResponseEntity.ok("Продукт успешно создан");
    }

    /**
     * Обновить существующий продукт.
     *
     * @param product_id  идентификатор продукта
     * @param productDto  новые данные продукта
     * @return сообщение об успехе
     */
    @Operation(summary = "Обновить продукт", description = "Обновляет данные существующего продукта.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Продукт не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    @PatchMapping("/{product_id}")
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "ID продукта", example = "1")
            @PathVariable Long product_id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Обновлённые данные продукта", required = true)
            @RequestBody @Validated ProductDto productDto) {
        productService.updateProduct(productDto, product_id);
        return ResponseEntity.ok("Продукт успешно обновлён");
    }

    /**
     * Удалить продукт по его ID.
     *
     * @param product_id идентификатор продукта
     * @return сообщение об успехе
     */
    @Operation(summary = "Удалить продукт", description = "Удаляет продукт из инвентаря по его ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Продукт не найден")
    })
    @DeleteMapping("/{product_id}")
    public ResponseEntity<?> deleteProduct(
            @Parameter(description = "ID продукта", example = "1")
            @PathVariable Long product_id) {
        productService.deleteProduct(product_id);
        return ResponseEntity.ok("Продукт успешно удалён");
    }
}
