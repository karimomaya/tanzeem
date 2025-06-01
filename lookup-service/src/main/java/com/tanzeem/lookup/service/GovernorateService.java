package com.tanzeem.lookup.service;

import com.tanzeem.lookup.dto.GovernorateResponse;

import java.util.List;

public interface GovernorateService {
    List<GovernorateResponse> getGovernorates(Long countryId);
    GovernorateResponse getGovernorateById(Long id);
}
