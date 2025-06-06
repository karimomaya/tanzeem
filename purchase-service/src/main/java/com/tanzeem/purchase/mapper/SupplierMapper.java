package com.tanzeem.purchase.mapper;


import com.tanzeem.common.client.LookupClient;
import com.tanzeem.common.dto.CountryResponse;
import com.tanzeem.common.dto.GovernorateResponse;
import com.tanzeem.common.mapper.LookupMapper;
import com.tanzeem.purchase.dto.SupplierRequest;
import com.tanzeem.purchase.dto.SupplierResponse;
import com.tanzeem.purchase.entity.Supplier;
import com.tanzeem.purchase.entity.lookup.BusinessType;
import com.tanzeem.purchase.entity.lookup.PaymentTerm;
import com.tanzeem.purchase.repository.lookup.BusinessTypeRepository;
import com.tanzeem.purchase.repository.lookup.PaymentTermRepository;
import com.tanzeem.security.common.AuthContextHolder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SupplierMapper {

    private final LookupClient lookupClient;
    private final LookupMapper lookupMapper;

    private final BusinessTypeRepository businessTypeRepository;
    private final PaymentTermRepository paymentTermRepository;


    public Supplier mapToEntity(SupplierRequest supplierRequest){
        BusinessType businessType = businessTypeRepository.findByCodeAndTenantId(supplierRequest.getBusinessType(), AuthContextHolder.getTenantId())
                .orElse(null);
        PaymentTerm paymentTerm = paymentTermRepository.findByCodeAndTenantId(supplierRequest.getPaymentTerm(), AuthContextHolder.getTenantId())
                .orElse(null);
        return Supplier.builder().id(supplierRequest.getId())
                .name(supplierRequest.getName())
                .code(supplierRequest.getCode())
                .contactPerson(supplierRequest.getContactPerson())
                .phone(supplierRequest.getPhone())
                .email(supplierRequest.getEmail())
                .isActive(supplierRequest.getIsActive())
                .address(supplierRequest.getAddress())
                .postalCode(supplierRequest.getPostalCode())
                .countryCode(supplierRequest.getCountry())
                .governorateCode(supplierRequest.getGovernorate())
                .businessType(businessType)
                .taxNumber(supplierRequest.getTaxNumber())
                .registrationNumber(supplierRequest.getRegistrationNumber())
                .paymentTerm(paymentTerm)
                .creditLimit(supplierRequest.getCreditLimit())
                .icon(supplierRequest.getIcon())
                .color(supplierRequest.getColor())
                .notes(supplierRequest.getNotes())
                .rating(0) // Default rating, can be updated later
                .build();
    }

    /**
     * Maps a Supplier entity to a SupplierResponse DTO.
     *
     * @param supplier the Supplier entity to map
     * @return the mapped SupplierResponse DTO
     */
    public SupplierResponse mapToResponse(Supplier supplier) {
        CountryResponse countryResponse = null;
        GovernorateResponse governorateResponse = null;
        if (supplier.getCountryCode() != null && !supplier.getCountryCode().isEmpty()) {
            try{ countryResponse = lookupClient.getCountryByCode(supplier.getCountryCode()); }catch (Exception ex) {ex.printStackTrace(); }
        }
        if (supplier.getGovernorateCode() != null && !supplier.getGovernorateCode().isEmpty()) {
                try{ governorateResponse = lookupClient.getGovernorateByCode(supplier.getGovernorateCode()); }catch (Exception ex) {ex.printStackTrace(); }
        }
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
                .businessType( lookupMapper.mapToResponse(supplier.getBusinessType()))
                .taxNumber(supplier.getTaxNumber())
                .registrationNumber(supplier.getRegistrationNumber())
                .paymentTerm(lookupMapper.mapToResponse(supplier.getPaymentTerm()))
                .creditLimit(supplier.getCreditLimit())
                .icon(supplier.getIcon())
                .color(supplier.getColor())
                .notes(supplier.getNotes())
                .rating(supplier.getRating())
                .build();
    }
}
