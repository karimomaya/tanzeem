package com.tanzeem.product.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductStatsResponse {
    private Long total;
    private Long active;
    private Long lowStock;
    private Long outOfStock;
    private Long withoutMinimumStock; // New field
    private List<CategoryProductCountResponse> productsByCategory; // New field
}
