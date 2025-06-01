package com.tanzeem.product.repository;

import com.tanzeem.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTenantId(String tenantId);

    @Query("SELECT MIN(p.minimumStock) FROM Product p WHERE p.tenantId = :tenantId")
    Integer getMinimumStockThreshold(@Param("tenantId") String tenantId);

    List<Product> findByTenantIdAndStockLessThan(String tenantId, Integer stockThreshold);
    Page<Product> findByTenantId(String tenantId, Pageable pageable);
    @Query(value = "SELECT p " +
            "FROM Product p " +
            "WHERE p.tenantId = :tenantId " +
            "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "ORDER BY " +
            "CASE " +
            "   WHEN p.stock = 0 THEN 1 " + // Out of stock
            "   WHEN p.stock <= p.minimumStock THEN 2 " + // Low stock
            "   ELSE 3 " + // Active
            "END ASC, " + // Sort by productStatus
            "p.name ASC",
            countQuery = "SELECT COUNT(p) " +
                    "FROM Product p " +
                    "WHERE p.tenantId = :tenantId " +
                    "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> findByTenantIdAndNameContainingIgnoreCase(@Param("tenantId") String tenantId,
                                                            @Param("name") String name,
                                                            Pageable pageable);
}