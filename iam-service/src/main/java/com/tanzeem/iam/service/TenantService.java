package com.tanzeem.iam.service;

import com.tanzeem.iam.entity.TenantSettings;
import com.tanzeem.iam.repository.TenantSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantService {
    private final TenantSettingsRepository tenantSettingsRepository;

    public TenantSettings createTenant(TenantSettings settings) {
        return tenantSettingsRepository.save(settings);
    }

    public TenantSettings findByTenantId(String tenantId) {
        return tenantSettingsRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id: " + tenantId));
    }
}
