package com.tanzeem.purchase.repository;

import com.tanzeem.purchase.entity.lookup.BusinessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessTypeRepository extends JpaRepository<BusinessType, Long> {
    List<BusinessType> findByTenantIdByOrderByNameAsc(String tenantId);
    Optional<BusinessType> findByIdAndTenantId(Long id, String tenantId);
    Optional<BusinessType> findByCode(String code);
}
