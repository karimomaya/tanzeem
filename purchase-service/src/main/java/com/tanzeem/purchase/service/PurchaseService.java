package com.tanzeem.purchase.service;

import com.tanzeem.purchase.dto.PurchaseRequest;
import com.tanzeem.purchase.dto.PurchaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PurchaseService {
    PurchaseResponse createPurchase(PurchaseRequest purchaseRequest);
    Page<PurchaseResponse> getAll(String search, String isActive, Pageable pageable);
    PurchaseResponse getPurchaseById(long id);
    PurchaseResponse updatePurchase(long id, PurchaseRequest purchaseRequest);
    void deletePurchase(long id);
    PurchaseResponse markAsReceived(long id);
    Double getAverageResponseTimeInDays(long supplierId);
    Integer countBySupplierId(long supplierId);
    Long countOnTimeDeliveriesBySupplierId(long supplierId);

    PurchaseResponse markAsPartiallyReceived(long id);
    PurchaseResponse markAsCanceled(long id);
}
