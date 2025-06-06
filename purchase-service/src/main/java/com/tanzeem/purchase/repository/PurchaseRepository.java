package com.tanzeem.purchase.repository;

import com.tanzeem.purchase.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>{
    Integer countBySupplierIdAndTenantId(Long supplierId, String tenantId);
    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Purchase p WHERE p.tenantId = :tenantId")
    BigDecimal sumTotalAmountByTenantId(@Param("tenantId") String tenantId);


    @Query("SELECT SUM(p.totalAmount) FROM Purchase p WHERE p.tenantId = :tenantId AND p.createdAt BETWEEN :start AND :end")
    BigDecimal sumTotalAmountByTenantIdAndCreatedAtBetween(@Param("tenantId") String tenantId,
                                                           @Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end);

    @Query("SELECT AVG(p.totalAmount) FROM Purchase p WHERE p.tenantId = :tenantId AND p.createdAt BETWEEN :start AND :end")
    BigDecimal avgTotalAmountByTenantIdAndCreatedAtBetween(@Param("tenantId") String tenantId,
                                                           @Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end);


    @Query("SELECT COALESCE(AVG(p.totalAmount), 0) FROM Purchase p WHERE p.tenantId = :tenantId")
    BigDecimal avgTotalAmountByTenantId(@Param("tenantId") String tenantId);
    @Query("""
        SELECT p.supplier.id, s.name, COUNT(p), SUM(p.totalAmount)
        FROM Purchase p 
        JOIN Supplier s ON p.supplier.id = s.id 
        WHERE p.tenantId = :tenantId 
        GROUP BY p.supplier.id, s.name 
        ORDER BY SUM(p.totalAmount) DESC
    """)
    List<Object[]> findTopSupplierByTotalAmount(@Param("tenantId") String tenantId);

    @Query("""
            SELECT COUNT(p)
            FROM Purchase p
            WHERE p.tenantId = :tenantId
            AND p.deliveredAt IS NOT NULL
            AND p.expectedDeliveryAt IS NOT NULL
            AND p.deliveredAt <= p.expectedDeliveryAt
       """)
    long countOnTimeDeliveries(@Param("tenantId") String tenantId);

    long countByTenantId(String tenantId);
    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Purchase p WHERE p.supplier.id = :supplierId")
    BigDecimal getTotalPurchaseAmountBySupplier(@Param("supplierId") Long supplierId);

    @Query("""
    SELECT COUNT(p)
    FROM Purchase p
    WHERE p.supplier.id = :supplierId
      AND p.deliveredAt IS NOT NULL
      AND p.deliveredAt <= p.expectedDeliveryAt AND p.tenantId = :tenantId
    """)
    Long countOnTimeDeliveriesBySupplierId(@Param("supplierId") Long supplierId, @Param("tenantId") String tenantId);

    @Query(value = """
    SELECT AVG(DATEDIFF(confirmed_date, created_at))
    FROM purchase
    WHERE supplier_id = :supplierId AND confirmed_date IS NOT NULL AND tenant_id = :tenantId
    """, nativeQuery = true)
    Optional<Double> getAverageResponseTimeInDays(@Param("supplierId") Long supplierId, @Param("tenantId") String tenantId);

    Page<Purchase> findByTenantIdAndInvoiceNumberContainingIgnoreCase(String tenantId, String invoiceNumber, Pageable pageable);

    Page<Purchase> findByIsActiveAndTenantIdAndInvoiceNumberContainingIgnoreCase(Boolean isActiveParam, String tenantId, String invoiceNumber, Pageable pageable);
}
