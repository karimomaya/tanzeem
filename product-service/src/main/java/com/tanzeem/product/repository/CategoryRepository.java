package com.tanzeem.product.repository;

import com.tanzeem.product.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Count total categories
    @Query("SELECT COUNT(c) FROM Category c WHERE c.tenantId = :tenantId")
    Long countByTenantId(@Param("tenantId") String tenantId);

    // Count active categories
    @Query("SELECT COUNT(c) FROM Category c WHERE c.tenantId = :tenantId AND c.isActive = true")
    Long countActiveCategories(@Param("tenantId") String tenantId);

    // Count inactive categories
    @Query("SELECT COUNT(c) FROM Category c WHERE c.tenantId = :tenantId AND c.isActive = false")
    Long countInactiveCategories(@Param("tenantId") String tenantId);

    // Get category with most products
    @Query(value = "SELECT c.name as categoryName, COUNT(p.id) as productCount " +
            "FROM categories c LEFT JOIN products p ON p.category_id = c.id AND p.tenant_id = :tenantId " +
            "WHERE c.tenant_id = :tenantId " +
            "GROUP BY c.id, c.name " +
            "ORDER BY COUNT(p.id) DESC", nativeQuery = true)
    List<Object[]> getCategoriesWithProductCount(@Param("tenantId") String tenantId);

    // Count categories without products
    @Query(value = "SELECT COUNT(*) FROM categories c " +
            "WHERE c.tenant_id = :tenantId " +
            "AND c.id NOT IN (SELECT DISTINCT p.category_id FROM products p WHERE p.tenant_id = :tenantId AND p.category_id IS NOT NULL)", nativeQuery = true)
    Long countCategoriesWithoutProducts(@Param("tenantId") String tenantId);

    // Calculate average products per category
    @Query(value = "SELECT COALESCE(AVG(product_count), 0) " +
            "FROM (SELECT COUNT(p.id) as product_count " +
            "      FROM categories c LEFT JOIN products p ON p.category_id = c.id AND p.tenant_id = :tenantId " +
            "      WHERE c.tenant_id = :tenantId " +
            "      GROUP BY c.id) subquery", nativeQuery = true)
    Double getAverageProductsPerCategory(@Param("tenantId") String tenantId);

    @Query(value = "SELECT c, " +
            "(CASE WHEN c.isActive = true THEN 50 ELSE 10 END + " +
            "LEAST(COALESCE(COUNT(p.id) * 5, 0), 40)) AS activity, " +
            "COUNT(p.id) AS productCount " +
            "FROM Category c LEFT JOIN Product p ON p.category.id = c.id " +
            "WHERE c.tenantId = :tenantId " +
            "AND (:isActive IS NULL OR c.isActive = :isActive) " +
            "AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "GROUP BY c.id",
            countQuery = "SELECT COUNT(c.id) " +
                    "FROM Category c LEFT JOIN Product p ON p.category.id = c.id " +
                    "WHERE c.tenantId = :tenantId " +
                    "AND (:isActive IS NULL OR c.isActive = :isActive) " +
                    "AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
                    "GROUP BY c.id")
    Page<Object[]> findByTenantIdAndIsActiveAndNameContainingIgnoreCase(@Param("tenantId") String tenantId,
                                                                        @Param("search") String search,
                                                                        @Param("isActive") Boolean isActive,
                                                                        Pageable pageable);


    Page<Category> findByTenantIdAndIsActive(String tenantId, boolean isActive, Pageable pageable);
    @Query("SELECT COUNT(p.id) FROM Product p WHERE p.category.id = :categoryId")
    Long countProductsByCategoryId(@Param("categoryId") Long categoryId);
}
