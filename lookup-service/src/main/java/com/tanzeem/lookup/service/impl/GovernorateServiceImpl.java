package com.tanzeem.lookup.service.impl;

import com.tanzeem.common.dto.CountryResponse;
import com.tanzeem.common.dto.GovernorateResponse;
import com.tanzeem.lookup.entity.Governorate;
import com.tanzeem.lookup.mapper.CountryMapper;
import com.tanzeem.lookup.repository.GovernorateRepository;
import com.tanzeem.lookup.service.CountryService;
import com.tanzeem.lookup.service.GovernorateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class GovernorateServiceImpl implements GovernorateService {

    private final GovernorateRepository governorateRepository;
    private final CountryService countryService;
    private final CountryMapper countryMapper;

    @Override
    @Cacheable(value = "governorates", key = "#countryCode")
    public List<GovernorateResponse> getGovernorateByCountryCode(String countryCode) {
        List<Governorate> governorates;

        if (countryCode == null) throw new InvalidParameterException("Invalid Parameter: Country code cannot be null");


        CountryResponse countryResponse = countryService.getCountryByCode(countryCode);
        governorates = governorateRepository.findByCountryId(countryResponse.getId());

        return governorates.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "governorate", key = "#code")
    public GovernorateResponse getGovernorateByCode(String code) {
        GovernorateResponse governorateResponse = governorateRepository.findByCode(code)
                .map(this::mapToResponse)
                .orElseThrow(() -> new EntityNotFoundException("Governorate not found with code: " + code));
        return governorateResponse;
    }


    private GovernorateResponse mapToResponse(Governorate g) {

        return GovernorateResponse.builder()
                .country(countryMapper.mapToResponse(g.getCountry()))
                .id(g.getId())
                .name(g.getName())
                .code(g.getCode())
                .isActive(g.isActive())
                .createdAt(g.getCreatedAt())
                .updatedAt(g.getUpdatedAt())
                .description(g.getDescription())
                .createdBy(g.getCreatedBy())
                .updatedBy(g.getUpdatedBy())
                .build();
    }
}
