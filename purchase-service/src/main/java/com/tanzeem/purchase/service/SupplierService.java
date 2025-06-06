package com.tanzeem.purchase.service;

import com.tanzeem.purchase.dto.SupplierRequest;
import com.tanzeem.purchase.dto.SupplierResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierService {
    Page<SupplierResponse> getAll(String search, String isActive, Pageable pageable);
    SupplierResponse update(Long id, SupplierRequest supplierRequest);
    void delete(Long id);
}
