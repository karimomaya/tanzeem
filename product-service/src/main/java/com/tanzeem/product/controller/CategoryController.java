package com.tanzeem.product.controller;

import com.tanzeem.product.dto.CategoryRequest;
import com.tanzeem.product.dto.CategoryResponse;
import com.tanzeem.product.dto.CategoryStatsResponse;
import com.tanzeem.product.dto.ProductStatsResponse;
import com.tanzeem.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('CATEGORY_CREATE')")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(Pageable pageable,  @RequestParam(required = false) String search,  @RequestParam(required = false, defaultValue = "true") String isActive ) {
        return ResponseEntity.ok(categoryService.getAllCategories(search, isActive, pageable));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('CATEGORY_CREATE')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                           @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('CATEGORY_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<CategoryStatsResponse> getCategoryStats() {
        return ResponseEntity.ok(categoryService.getCategoryStats());
    }
}
