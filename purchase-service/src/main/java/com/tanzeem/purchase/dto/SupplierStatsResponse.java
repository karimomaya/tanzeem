package com.tanzeem.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierStatsResponse {
    private long total;
    private long active;
    private long newThisMonth;
    private BigDecimal averageRating;

    private BigDecimal totalSpending;
    private BigDecimal averageOrderValue;

    private TopSupplierResponse topSupplier;

    private double onTimeDeliveryRate;

    private Double totalGrowth;
    private Double newSuppliersGrowth;
    private BigDecimal ratingTrend;
    private BigDecimal spendingGrowth;
    private BigDecimal averageOrderTrend;
    private Double deliveryTrend;
}
