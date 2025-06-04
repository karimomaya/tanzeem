package com.tanzeem.purchase.repository;

import com.tanzeem.purchase.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Page<Supplier> findByIsActiveAndTenantIdAndNameContainingIgnoreCase( boolean isActive, String tenantId, String name, Pageable pageable);

    Page<Supplier> findByTenantIdAndNameContainingIgnoreCase(  String tenantId, String name, Pageable pageable);
}
