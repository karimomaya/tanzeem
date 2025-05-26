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
    Page<Product> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name, Pageable pageable);
}