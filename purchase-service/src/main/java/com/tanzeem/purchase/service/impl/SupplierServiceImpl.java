package com.tanzeem.purchase.service.impl;

import com.tanzeem.common.client.LookupClient;
import com.tanzeem.common.dto.CountryResponse;
import com.tanzeem.common.dto.GovernorateResponse;
import com.tanzeem.purchase.dto.SupplierResponse;
import com.tanzeem.purchase.entity.Supplier;
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
    private final LookupClient lookupClient;
    @Override
    public Page<SupplierResponse> getAll(String search, String isActive, Pageable pageable) {
        if (search == null) search = "";
        Boolean isActiveParam = "all".equalsIgnoreCase(isActive) ? null : Boolean.valueOf(isActive);
        Page<Supplier> suppliers;
        if (isActiveParam == null) suppliers = supplierRepository.findByTenantIdAndNameContainingIgnoreCase(AuthContextHolder.getTenantId(),search , pageable);
        else suppliers = supplierRepository.findByIsActiveAndTenantIdAndNameContainingIgnoreCase(isActiveParam, AuthContextHolder.getTenantId(),search , pageable);

        return suppliers.map(this::mapToResponse);
    }

    private SupplierResponse mapToResponse(Supplier supplier){
        CountryResponse countryResponse = null;
        GovernorateResponse governorateResponse = null;
        try{ countryResponse = lookupClient.getCountryByCode(supplier.getCountryCode()); }catch (Exception ex) {ex.printStackTrace(); }
        try{ governorateResponse = lookupClient.getGovernorateByCode(supplier.getGovernorateCode()); }catch (Exception ex) {ex.printStackTrace(); }
        return SupplierResponse.builder().id(supplier.getId())
                .name(supplier.getName())
                .code(supplier.getCode())
                .contactPerson(supplier.getContactPerson())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .isActive(supplier.getIsActive())
                .address(supplier.getAddress())
                .postalCode(supplier.getPostalCode())
                .country(countryResponse)
                .governorate(governorateResponse)
                .businessType(supplier.getBusinessType())
                .taxNumber(supplier.getTaxNumber())
                .registrationNumber(supplier.getRegistrationNumber())
                .paymentTerms(supplier.getPaymentTerms())
                .creditLimit(supplier.getCreditLimit())
                .icon(supplier.getIcon())
                .color(supplier.getColor())
                .notes(supplier.getNotes())
                .build();
    }
}
