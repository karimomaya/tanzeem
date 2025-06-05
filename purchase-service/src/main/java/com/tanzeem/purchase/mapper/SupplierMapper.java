package com.tanzeem.purchase.mapper;


import com.tanzeem.common.client.LookupClient;
import com.tanzeem.common.dto.CountryResponse;
import com.tanzeem.common.dto.GovernorateResponse;
import com.tanzeem.common.mapper.LookupMapper;
import com.tanzeem.purchase.dto.SupplierResponse;
import com.tanzeem.purchase.entity.Supplier;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SupplierMapper {

    private final LookupClient lookupClient;
    private final LookupMapper lookupMapper;
    /**
     * Maps a Supplier entity to a SupplierResponse DTO.
     *
     * @param supplier the Supplier entity to map
     * @return the mapped SupplierResponse DTO
     */
    public SupplierResponse mapToResponse(Supplier supplier) {
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
