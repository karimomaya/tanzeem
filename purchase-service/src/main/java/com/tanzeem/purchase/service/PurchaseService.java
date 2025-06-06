package com.tanzeem.purchase.service;

import com.tanzeem.purchase.dto.PurchaseRequest;
import com.tanzeem.purchase.dto.PurchaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PurchaseService {
    PurchaseResponse createPurchase(PurchaseRequest purchaseRequest);
    Page<PurchaseResponse> getAllPurchases(Pageable pageable);
    PurchaseResponse getPurchaseById(long id);
    PurchaseResponse updatePurchase(long id, PurchaseRequest purchaseRequest);
    void deletePurchase(long id);
    PurchaseResponse markAsReceived(long id);
    Double getAverageResponseTimeInDays(long supplierId);
    Integer countBySupplierId(long supplierId);
    Long countOnTimeDeliveries(long supplierId);
}
