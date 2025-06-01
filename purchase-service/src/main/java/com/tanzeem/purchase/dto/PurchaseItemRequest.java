package com.tanzeem.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PurchaseItemRequest {
    private Long productId;
    private String productName; // optional but useful snapshot
    private Integer quantity;
    private BigDecimal unitPrice;
}
