package com.tanzeem.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequest {
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private Boolean isActive = true;
}
