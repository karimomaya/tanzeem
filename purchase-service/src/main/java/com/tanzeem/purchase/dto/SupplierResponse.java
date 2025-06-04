package com.tanzeem.purchase.dto;

import com.tanzeem.common.dto.CountryResponse;
import com.tanzeem.common.dto.GovernorateResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SupplierResponse {
    private Long id;
    private String name;
    private String code;
    private String contactPerson;
    private String phone;
    private String email;
    private Boolean isActive;
    private String address;
    private String postalCode;
    private CountryResponse country;
    private GovernorateResponse governorate;
    private String businessType;
    private String taxNumber;
    private String registrationNumber;
    private String paymentTerms;
    private Double creditLimit;
    private String icon;
    private String color;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
