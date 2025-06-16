package com.tanzeem.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequest {
    private Long supplierId;
    private String invoiceNumber;
    private String status;
    private LocalDate purchaseDate;
    private LocalDateTime expectedDeliveryAt;
    private LocalDateTime deliveredAt;
    private LocalDate confirmedAt;
    private List<PurchaseItemRequest> items;
    private String notes;
    private BigDecimal totalAmount;

}