package com.tanzeem.product.controller;

import com.tanzeem.product.dto.ProductDto;
import com.tanzeem.product.dto.ProductResponse;
import com.tanzeem.product.entity.Product;
import com.tanzeem.product.service.ProductService;
import com.tanzeem.security.common.AuthContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public ResponseEntity<Product> create(@RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.create(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAll(Pageable pageable, @RequestParam(required = false) String search) {
        return ResponseEntity.ok(productService.getAll(search, pageable));
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

    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, String>> uploadProductImage(@RequestParam("image") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("File must be an image");
            }

            String dir = uploadDir + "/"+ AuthContextHolder.getTenantId();
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(dir, "products");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Generate URL
            String imageUrl = baseUrl + "/api/products/images/" + uniqueFilename;

            // Return response
            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            response.put("filename", uniqueFilename);
            response.put("originalName", originalFilename);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable String filename) {
        try {
            String dir = uploadDir + "/"+ AuthContextHolder.getTenantId();
            Path filePath = Paths.get(dir, "products", filename);

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(filePath);

            // Determine content type based on file extension
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(imageBytes);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
