package com.tanzeem.lookup.service;

import com.tanzeem.common.dto.GovernorateResponse;

import java.util.List;

public interface GovernorateService {
    List<GovernorateResponse> getGovernorateByCountryCode(String countryCode);
    GovernorateResponse getGovernorateByCode(String code);

}
