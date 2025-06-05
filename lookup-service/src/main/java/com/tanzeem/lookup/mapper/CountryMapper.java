package com.tanzeem.lookup.mapper;

import com.tanzeem.common.dto.CountryResponse;
import com.tanzeem.lookup.entity.Country;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CountryMapper {
    public CountryResponse mapToResponse(Country country) {
        return CountryResponse.builder()
                .id(country.getId())
                .code(country.getCode())
                .name(country.getName())
                .flagIcon(country.getFlagIcon())
                .description(country.getDescription())
                .isActive(country.isActive())
                .createdAt(country.getCreatedAt())
                .updatedAt(country.getUpdatedAt())
                .createdBy(country.getCreatedBy())
                .updatedBy(country.getUpdatedBy())
                .build();
    }
}
