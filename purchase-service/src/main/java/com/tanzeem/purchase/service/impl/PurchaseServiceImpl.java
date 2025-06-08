package com.tanzeem.purchase.service.impl;

import com.tanzeem.purchase.dto.PurchaseRequest;
import com.tanzeem.purchase.dto.PurchaseResponse;
import com.tanzeem.purchase.entity.Purchase;
import com.tanzeem.purchase.entity.PurchaseItem;
import com.tanzeem.purchase.entity.Supplier;
import com.tanzeem.purchase.enums.PurchaseStatus;
import com.tanzeem.purchase.mapper.PurchaseMapper;
import com.tanzeem.purchase.repository.PurchaseItemRepository;
import com.tanzeem.purchase.repository.PurchaseRepository;
import com.tanzeem.purchase.repository.SupplierRepository;
import com.tanzeem.purchase.service.PurchaseService;
import com.tanzeem.security.common.AuthContextHolder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final PurchaseMapper purchaseMapper;

    @Override
    public PurchaseResponse createPurchase(PurchaseRequest purchaseRequest) {
        Supplier supplier = supplierRepository.findById(purchaseRequest.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));

        Purchase purchase = purchaseMapper.toEntity(purchaseRequest);
        purchase.setSupplier(supplier);
        purchase.setStatus(PurchaseStatus.PENDING); // default status

        purchase.getItems().forEach(item -> item.setPurchase(purchase));
        purchaseRepository.save(purchase);

        return purchaseMapper.toResponse(purchase);
    }

    @Override
    public Page<PurchaseResponse> getAll(String search, String isActive, Pageable pageable) {

        if (search == null) search = "";
        Boolean isActiveParam = "all".equalsIgnoreCase(isActive) ? null : Boolean.valueOf(isActive);
        Page<Purchase> purchases;
        if (isActiveParam == null) purchases = purchaseRepository.findByTenantIdAndInvoiceNumberContainingIgnoreCase(AuthContextHolder.getTenantId(),search , pageable);
        else purchases = purchaseRepository.findByIsActiveAndTenantIdAndInvoiceNumberContainingIgnoreCase(isActiveParam, AuthContextHolder.getTenantId(),search , pageable);

        return purchases.map(purchaseMapper::toResponse);
    }

    @Override
    public PurchaseResponse getPurchaseById(long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));
        return purchaseMapper.toResponse(purchase);
    }

    @Override
    public PurchaseResponse updatePurchase(long id, PurchaseRequest request) {
        Purchase existing = purchaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));

        existing.setSupplier(supplier);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.getItems().clear();

        List<PurchaseItem> updatedItems = request.getItems().getContent().stream()
                .flatMap(List::stream)
                .map(purchaseMapper::toItemEntity)
                .peek(item -> item.setPurchase(existing)) // Ensure setPurchase exists in PurchaseItem
                .toList();

        existing.getItems().addAll(updatedItems);
        purchaseRepository.save(existing);

        return purchaseMapper.toResponse(existing);
    }

    @Override
    public void deletePurchase(long id) {
        if (!purchaseRepository.existsById(id)) {
            throw new EntityNotFoundException("Purchase not found");
        }
        purchaseRepository.deleteById(id);
    }


    @Override
    public PurchaseResponse markAsReceived(long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));
        purchase.setStatus(PurchaseStatus.RECEIVED);
        purchaseRepository.save(purchase);
        return purchaseMapper.toResponse(purchase);
    }

    @Override
    public Double getAverageResponseTimeInDays(long supplierId) {
        return purchaseRepository
                .getAverageResponseTimeInDays(supplierId, AuthContextHolder.getTenantId())
                .orElse(0.0);
    }

    @Override
    public Integer countBySupplierId(long supplierId) {
        return purchaseRepository.countBySupplierIdAndTenantId(supplierId, AuthContextHolder.getTenantId());
    }

    @Override
    public Long countOnTimeDeliveriesBySupplierId(long supplierId) {
        return purchaseRepository.countOnTimeDeliveriesBySupplierId(supplierId, AuthContextHolder.getTenantId());
    }

    public Integer countBySupplierId(Long supplierId) {
        return purchaseRepository.countBySupplierIdAndTenantId(supplierId, AuthContextHolder.getTenantId());
    }
}
