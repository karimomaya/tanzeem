package com.tanzeem.purchase.mapper;

import com.tanzeem.purchase.dto.PurchaseItemResponse;
import com.tanzeem.purchase.dto.PurchaseRequest;
import com.tanzeem.purchase.entity.Purchase;
import com.tanzeem.purchase.entity.PurchaseItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PurchaseItemMapper {
    public PurchaseItemResponse toResponse(PurchaseItem request) {
        return PurchaseItemResponse.builder().productId(request.getProductId())
                .id(request.getId())
                .unitPrice(request.getUnitPrice())
                .createdAt(request.getCreatedAt())
                .updatedBy(request.getUpdatedBy())
                .createdBy(request.getCreatedBy())
                .quantity(request.getQuantity())
                .productName(request.getProductNameAtPurchase())
                .build();

    }
}
