package com.tanzeem.purchase.controller;


import com.tanzeem.purchase.dto.PurchaseRequest;
import com.tanzeem.purchase.dto.PurchaseResponse;
import com.tanzeem.purchase.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<PurchaseResponse> createPurchase(@RequestBody PurchaseRequest purchaseRequest) {
        PurchaseResponse createdPurchase = purchaseService.createPurchase(purchaseRequest);
        return ResponseEntity.ok(createdPurchase);
    }

    @GetMapping
    public ResponseEntity<Page<PurchaseResponse>> getAll(Pageable pageable, @RequestParam(required = false) String search, @RequestParam(required = false, defaultValue = "true") String isActive ) {
        Page<PurchaseResponse> purchases = purchaseService.getAll(search, isActive, pageable);
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getPurchaseById(@PathVariable Long id) {
        PurchaseResponse purchase = purchaseService.getPurchaseById(id);
        return ResponseEntity.ok(purchase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseResponse> updatePurchase(@PathVariable Long id, @RequestBody PurchaseRequest purchaseRequest) {
        PurchaseResponse updatedPurchase = purchaseService.updatePurchase(id, purchaseRequest);
        return ResponseEntity.ok(updatedPurchase);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/mark-received")
    public ResponseEntity<PurchaseResponse> markPurchaseAsReceived(@PathVariable Long id) {
        PurchaseResponse updatedPurchase = purchaseService.markAsReceived(id);
        return ResponseEntity.ok(updatedPurchase);
    }

}
