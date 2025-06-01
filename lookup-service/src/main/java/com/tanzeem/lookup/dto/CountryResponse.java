package com.tanzeem.lookup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountryResponse {
    private Long id;
    private String name;
    private String code;
    private String flagIcon;
    private Boolean isActive;
}
