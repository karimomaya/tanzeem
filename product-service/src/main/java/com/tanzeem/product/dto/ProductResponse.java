package com.tanzeem.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    Long id;
    private String name;
    private String sku;
    private String barcode;
    private String unit;
    private BigDecimal price;
    private Integer stock;
    private String status; // Add this field
    private Integer minimumStock;
    private String imageUrl;
    private Long categoryId;
    private CategoryResponse category; // Use CategoryResponse for category details
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
