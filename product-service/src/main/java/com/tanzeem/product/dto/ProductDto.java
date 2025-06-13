package com.tanzeem.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private String name;
    private String sku;
    private String barcode;
    private String unit;
    private BigDecimal price;
    private Integer stock;
    private Integer minimumStock;
    private String[] imageUrls;
    private Long categoryId;
    private boolean isActive;
}
