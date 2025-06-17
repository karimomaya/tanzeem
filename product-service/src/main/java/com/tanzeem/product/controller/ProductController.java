package com.tanzeem.product.controller;

import com.tanzeem.product.dto.ProductDto;
import com.tanzeem.product.dto.ProductResponse;
import com.tanzeem.product.dto.ProductStatsResponse;
import com.tanzeem.product.entity.Product;
import com.tanzeem.product.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.create(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAll(
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String isActive,
            @RequestParam(required = false) String productStatus
    ) {
        return ResponseEntity.ok(productService.getAll(search, isActive, productStatus, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getById(id));
    }



    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStock() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }


    @GetMapping("/stats")
    public ResponseEntity<ProductStatsResponse> getProductStats() {
        ProductStatsResponse stats = ProductStatsResponse.builder()
                .total(productService.getTotalProductsCount())
                .active(productService.getActiveProductsCount())
                .lowStock(productService.getLowStockProductsCount())
                .outOfStock(productService.getOutOfStockProductsCount())
                .withoutMinimumStock(productService.getProductsWithoutMinimumStockCount()) // New
                .productsByCategory(productService.getProductCountByCategory()) // New
                .build();
        return ResponseEntity.ok(stats);
    }
}
