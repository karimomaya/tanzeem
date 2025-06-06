package com.tanzeem.purchase.repository;

import com.tanzeem.purchase.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    long countByTenantId(String tenantId);
    long countByIsActiveTrueAndTenantId(String tenantId);
    long countByCreatedAtBetweenAndTenantId(LocalDateTime start, LocalDateTime end, String tenantId);
    Optional<Supplier> findByIdAndTenantId(Long id, String tenantId);
    Page<Supplier> findByIsActiveAndTenantIdAndNameContainingIgnoreCase( boolean isActive, String tenantId, String name, Pageable pageable);

    Page<Supplier> findByTenantIdAndNameContainingIgnoreCase(  String tenantId, String name, Pageable pageable);
}
