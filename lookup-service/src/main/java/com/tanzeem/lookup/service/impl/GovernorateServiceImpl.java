package com.tanzeem.lookup.service.impl;

import com.tanzeem.common.dto.CountryResponse;
import com.tanzeem.lookup.dto.GovernorateResponse;
import com.tanzeem.lookup.entity.Governorate;
import com.tanzeem.lookup.repository.GovernorateRepository;
import com.tanzeem.lookup.service.CountryService;
import com.tanzeem.lookup.service.GovernorateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class GovernorateServiceImpl implements GovernorateService {

    private final GovernorateRepository governorateRepository;
    private final CountryService countryService;

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


    private GovernorateResponse mapToResponse(Governorate g) {
        GovernorateResponse dto = new GovernorateResponse();
        dto.setId(g.getId());
        dto.setName(g.getName());
        dto.setCode(g.getCode());
        dto.setIsActive(g.isActive());
        dto.setCountryId(g.getCountry().getId());
        dto.setCountryName(g.getCountry().getName());
        return dto;
    }
}
