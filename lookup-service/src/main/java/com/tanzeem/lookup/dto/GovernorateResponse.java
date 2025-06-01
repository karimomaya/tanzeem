package com.tanzeem.lookup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GovernorateResponse {
    private Long id;
    private String name;
    private Long countryId;
    private String countryName;
    private Boolean isActive;
}
