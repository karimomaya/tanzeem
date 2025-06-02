package com.tanzeem.lookup.service.impl;

import com.tanzeem.lookup.dto.GovernorateResponse;
import com.tanzeem.lookup.entity.Governorate;
import com.tanzeem.lookup.repository.GovernorateRepository;
import com.tanzeem.lookup.service.GovernorateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class GovernorateServiceImpl implements GovernorateService {

    private final GovernorateRepository governorateRepository;

    @Override
    @Cacheable("governorates")
    public List<GovernorateResponse> getGovernorates(Long countryId) {
        List<Governorate> governorates;

        if (countryId != null) {
            governorates = governorateRepository.findByCountryId(countryId);
        } else {
            governorates = governorateRepository.findAll();
        }

        return governorates.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GovernorateResponse getGovernorateById(Long id) {
        Governorate governorate = governorateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Governorate not found"));
        return mapToResponse(governorate);
    }

    private GovernorateResponse mapToResponse(Governorate g) {
        GovernorateResponse dto = new GovernorateResponse();
        dto.setId(g.getId());
        dto.setName(g.getName());
        dto.setIsActive(g.getIsActive());
        dto.setCountryId(g.getCountry().getId());
        dto.setCountryName(g.getCountry().getName());
        return dto;
    }
}
