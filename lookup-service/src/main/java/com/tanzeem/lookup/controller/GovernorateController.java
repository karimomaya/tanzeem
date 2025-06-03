package com.tanzeem.lookup.controller;


import com.tanzeem.lookup.dto.GovernorateResponse;
import com.tanzeem.lookup.service.GovernorateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/governorates")
@RequiredArgsConstructor
public class GovernorateController {
    private final GovernorateService governorateService;

    @GetMapping
    public List<GovernorateResponse> getAll(@RequestParam(required = false) String countryCode) {
        return governorateService.getGovernorateByCountryCode(countryCode);
    }
}
