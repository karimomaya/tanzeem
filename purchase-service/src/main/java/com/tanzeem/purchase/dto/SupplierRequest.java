package com.tanzeem.purchase.dto;

import com.tanzeem.common.dto.CountryResponse;
import com.tanzeem.common.dto.GovernorateResponse;
import com.tanzeem.common.dto.LookupResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequest {
    private Long id;
    private String name;
    private String code;
    private String contactPerson;
    private String phone;
    private String email;
    private Boolean isActive;
    private String address;
    private String postalCode;
    private String country;
    private String governorate;
    private String businessType;
    private String taxNumber;
    private String registrationNumber;
    private String paymentTerm;
    private Double creditLimit;
    private String icon;
    private String color;
    private String notes;
}
