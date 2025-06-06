package com.tanzeem.purchase.repository;

import com.tanzeem.purchase.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>{
    Integer countBySupplierIdAndTenantId(Long supplierId, String tenantId);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Purchase p WHERE p.supplier.id = :supplierId")
    BigDecimal getTotalPurchaseAmountBySupplier(@Param("supplierId") Long supplierId);

    @Query("""
    SELECT COUNT(p) 
    FROM Purchase p 
    WHERE p.supplier.id = :supplierId 
      AND p.actualDeliveryDate IS NOT NULL 
      AND p.actualDeliveryDate <= p.expectedDeliveryDate AND p.tenantId = :tenantId
    """)
    Long countOnTimeDeliveries(@Param("supplierId") Long supplierId, @Param("tenantId") String tenantId);

    @Query(value = """
    SELECT AVG(DATEDIFF(confirmed_date, created_at))
    FROM purchase
    WHERE supplier_id = :supplierId AND confirmed_date IS NOT NULL AND tenant_id = :tenantId
    """, nativeQuery = true)
    Optional<Double> getAverageResponseTimeInDays(@Param("supplierId") Long supplierId, @Param("tenantId") String tenantId);
}
