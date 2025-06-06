package com.tanzeem.purchase.repository.lookup;

import com.tanzeem.purchase.entity.lookup.PaymentTerm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTermRepository extends JpaRepository<PaymentTerm, Long> {
    List<PaymentTerm> findByTenantIdOrderByNameAsc(String tenantId);
    Optional<PaymentTerm> findByCodeAndTenantId(String code, String tenantId);

}
