package com.tanzeem.purchase.dto.lookup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessTypeResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private boolean active;
    private boolean deleted;
}
