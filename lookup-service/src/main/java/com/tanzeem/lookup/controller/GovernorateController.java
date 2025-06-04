package com.tanzeem.lookup.controller;


import com.tanzeem.common.dto.GovernorateResponse;
import com.tanzeem.lookup.service.GovernorateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/governorates")
@RequiredArgsConstructor
public class GovernorateController {
    private final GovernorateService governorateService;

    @GetMapping
    public List<GovernorateResponse> getGovernorateByCountryCode(@RequestParam(required = false) String countryCode) {
        return governorateService.getGovernorateByCountryCode(countryCode);
    }

    @GetMapping("{code}")
    public GovernorateResponse getGovernorateByCode(@PathVariable String code) {
        return governorateService.getGovernorateByCode(code);
    }
}
