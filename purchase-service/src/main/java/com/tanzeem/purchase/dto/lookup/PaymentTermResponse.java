package com.tanzeem.purchase.dto.lookup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentTermResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private boolean active;
    private boolean deleted;

}
