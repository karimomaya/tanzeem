package com.tanzeem.purchase.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PurchaseResponse {
    private Long id;
    private LocalDate purchaseDate;
    private String invoiceNumber;
    private BigDecimal totalAmount;
    private SupplierResponse supplier;
    private List<PurchaseItemResponse> items;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime expectedDeliveryAt;
    private String createdBy;
    private String updatedBy;
    private String status;

}
