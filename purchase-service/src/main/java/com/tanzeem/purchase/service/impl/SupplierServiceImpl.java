package com.tanzeem.purchase.service.impl;

import com.tanzeem.common.client.LookupClient;
import com.tanzeem.common.dto.CountryResponse;
import com.tanzeem.common.dto.GovernorateResponse;
import com.tanzeem.purchase.dto.SupplierResponse;
import com.tanzeem.purchase.entity.Supplier;
import com.tanzeem.purchase.mapper.SupplierMapper;
import com.tanzeem.purchase.repository.SupplierRepository;
import com.tanzeem.purchase.service.SupplierService;
import com.tanzeem.security.common.AuthContextHolder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    @Override
    public Page<SupplierResponse> getAll(String search, String isActive, Pageable pageable) {
        if (search == null) search = "";
        Boolean isActiveParam = "all".equalsIgnoreCase(isActive) ? null : Boolean.valueOf(isActive);
        Page<Supplier> suppliers;
        if (isActiveParam == null) suppliers = supplierRepository.findByTenantIdAndNameContainingIgnoreCase(AuthContextHolder.getTenantId(),search , pageable);
        else suppliers = supplierRepository.findByIsActiveAndTenantIdAndNameContainingIgnoreCase(isActiveParam, AuthContextHolder.getTenantId(),search , pageable);

        return suppliers.map(supplierMapper::mapToResponse);
    }
}
