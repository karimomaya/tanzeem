package com.tanzeem.purchase.mapper;

import com.tanzeem.purchase.dto.*;
import com.tanzeem.purchase.entity.Purchase;
import com.tanzeem.purchase.entity.PurchaseItem;
import com.tanzeem.purchase.entity.Supplier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurchaseMapper {

    public Purchase toEntity(PurchaseRequest request) {
        Purchase purchase = new Purchase();
        List<PurchaseItem> items = request.getItems().getContent().stream()
        .flatMap(List::stream)
                .map(this::toItemEntity)
                .collect(Collectors.toList());
        purchase.setItems(items);
        return purchase;
    }

    public PurchaseItem toItemEntity(PurchaseItemRequest request) {
        PurchaseItem item = new PurchaseItem();
        item.setProductId(request.getProductId());
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        return item;
    }

    public PurchaseResponse toResponse(Purchase purchase) {
        return PurchaseResponse.builder()
                .invoiceNumber(purchase.getInvoiceNumber())
                .notes(purchase.getNotes())
                .purchaseDate(purchase.getPurchaseDate())
                .totalAmount(purchase.getTotalAmount())
                .build();

    }

    public PurchaseItemResponse toItemResponse(PurchaseItem item) {
        return PurchaseItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }

    public SupplierResponse toSupplierResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .contactPerson(supplier.getContactPerson())
                .isActive(supplier.getIsActive())
                .name(supplier.getName())
                .build();

    }
}
