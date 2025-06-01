package com.tanzeem.lookup.controller;


import com.tanzeem.lookup.dto.CountryResponse;
import com.tanzeem.lookup.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;

    @GetMapping
    public List<CountryResponse> getAll() {
        return countryService.getAllCountries();
    }
}
