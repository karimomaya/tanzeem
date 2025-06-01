package com.tanzeem.product.repository;

import com.tanzeem.product.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "SELECT c, " +
            "(CASE WHEN c.isActive = true THEN 50 ELSE 10 END + " +
            "LEAST(COALESCE(COUNT(p.id) * 5, 0), 40)) AS activity, " +
            "COUNT(p.id) AS productCount " +
            "FROM Category c LEFT JOIN Product p ON p.category.id = c.id " +
            "WHERE c.tenantId = :tenantId AND c.isActive = :isActive " +
            "AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "GROUP BY c.id",
            countQuery = "SELECT COUNT(c.id) " +
                    "FROM Category c LEFT JOIN Product p ON p.category.id = c.id " +
                    "WHERE c.tenantId = :tenantId AND c.isActive = :isActive " +
                    "AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
                    "GROUP BY c.id")
    Page<Object[]> findByTenantIdAndIsActiveAndNameContainingIgnoreCase(@Param("tenantId") String tenantId,  @Param("search") String search, @Param("isActive") boolean isActive,Pageable pageable);



    Page<Category> findByTenantIdAndIsActive(String tenantId, boolean isActive, Pageable pageable);
    @Query("SELECT COUNT(p.id) FROM Product p WHERE p.category.id = :categoryId")
    Long countProductsByCategoryId(@Param("categoryId") Long categoryId);
}
