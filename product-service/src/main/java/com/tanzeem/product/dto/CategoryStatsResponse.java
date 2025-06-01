package com.tanzeem.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryStatsResponse {
    private Long total;
    private Long active;
    private Long inactive;
    private Double averageProducts;
    private String topCategoryName;
    private Long topCategoryProductCount;
    private Long categoriesWithoutProducts;
    private Double activePercentage;
}
