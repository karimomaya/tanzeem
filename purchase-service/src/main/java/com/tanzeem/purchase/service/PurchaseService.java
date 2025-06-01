package com.tanzeem.purchase.service;

import com.tanzeem.purchase.dto.PurchaseRequest;
import com.tanzeem.purchase.dto.PurchaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PurchaseService {
    PurchaseResponse createPurchase(PurchaseRequest purchaseRequest);
    Page<PurchaseResponse> getAllPurchases(Pageable pageable);
    PurchaseResponse getPurchaseById(Long id);
    PurchaseResponse updatePurchase(Long id, PurchaseRequest purchaseRequest);
    void deletePurchase(Long id);
    PurchaseResponse markAsReceived(Long id);
}
