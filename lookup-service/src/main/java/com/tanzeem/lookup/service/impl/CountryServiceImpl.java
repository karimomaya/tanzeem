package com.tanzeem.lookup.service.impl;

import com.tanzeem.lookup.dto.CountryResponse;
import com.tanzeem.lookup.entity.Country;
import com.tanzeem.lookup.repository.CountryRepository;
import com.tanzeem.lookup.service.CountryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Override
    @Cacheable("countries")
    public List<CountryResponse> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public com.tanzeem.lookup.dto.CountryResponse getCountryById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Country not found"));
        return mapToResponse(country);
    }

    private CountryResponse mapToResponse(Country country) {
        CountryResponse response = new CountryResponse();
        response.setId(country.getId());
        response.setName(country.getName());
        response.setCode(country.getCode());
        response.setFlagIcon(country.getFlagIcon());
        response.setIsActive(country.getIsActive());
        return response;
    }
}
