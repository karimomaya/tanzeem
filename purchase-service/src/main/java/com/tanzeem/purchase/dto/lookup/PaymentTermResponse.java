package com.tanzeem.purchase.dto.lookup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTermResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private boolean active;
    private boolean deleted;

}
