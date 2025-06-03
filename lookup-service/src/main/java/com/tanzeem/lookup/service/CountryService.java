package com.tanzeem.lookup.service;

import com.tanzeem.lookup.dto.CountryResponse;

import java.util.List;

public interface CountryService {
    List<CountryResponse> getAllCountries();
    CountryResponse getCountryById(Long id);
    CountryResponse getCountryByCode(String code);
}
