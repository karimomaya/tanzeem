package com.tanzeem.iam.repository;

import com.tanzeem.iam.entity.TenantSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantSettingsRepository extends JpaRepository<TenantSettings, Long> {
    Optional<TenantSettings> findByTenantId(String tenantId);
    boolean existsByTenantId(String tenantId);
}
