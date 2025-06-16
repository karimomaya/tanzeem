package com.tanzeem.product.repository;

import com.tanzeem.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT MIN(p.minimumStock) FROM Product p WHERE p.tenantId = :tenantId")
    Integer getMinimumStockThreshold(@Param("tenantId") String tenantId);

    // Total products count
    @Query("SELECT COUNT(p) FROM Product p WHERE p.tenantId = :tenantId")
    Long countByTenantId(@Param("tenantId") String tenantId);

    // Active products count (stock > minimumStock AND both product and category are active)
    @Query("SELECT COUNT(p) FROM Product p " +
            "WHERE p.tenantId = :tenantId " +
            "AND p.stock > p.minimumStock " +
            "AND p.isActive = true " +
            "AND p.category.isActive = true")
    Long countActiveProducts(@Param("tenantId") String tenantId);

    // Low stock products count (stock > 0 AND stock <= minimumStock AND both active)
    @Query("SELECT COUNT(p) FROM Product p " +
            "WHERE p.tenantId = :tenantId " +
            "AND p.stock > 0 " +
            "AND p.stock <= p.minimumStock " +
            "AND p.isActive = true " +
            "AND p.category.isActive = true")
    Long countLowStockProducts(@Param("tenantId") String tenantId);

    // Out of stock products count (stock = 0 AND both active)
    @Query("SELECT COUNT(p) FROM Product p " +
            "WHERE p.tenantId = :tenantId " +
            "AND p.stock = 0 " +
            "AND p.isActive = true " +
            "AND p.category.isActive = true")
    Long countOutOfStockProducts(@Param("tenantId") String tenantId);

    @Query("SELECT new map(c.name as categoryName, COUNT(p) as productCount) " +
            "FROM Product p JOIN p.category c " +
            "WHERE p.tenantId = :tenantId " +
            "GROUP BY c.id, c.name " +
            "ORDER BY COUNT(p) DESC")
    List<Map<String, Object>> countProductsByCategory(@Param("tenantId") String tenantId);

    // Count products without minimum stock set (null or 0)
    @Query("SELECT COUNT(p) FROM Product p " +
            "WHERE p.tenantId = :tenantId " +
            "AND (p.minimumStock IS NULL OR p.minimumStock = 0)")
    Long countProductsWithoutMinimumStock(@Param("tenantId") String tenantId);

    List<Product> findByTenantIdAndStockLessThan(String tenantId, Integer stockThreshold);
    Page<Product> findByTenantId(String tenantId, Pageable pageable);
    @Query(value = "SELECT p " +
            "FROM Product p " +
            "WHERE p.tenantId = :tenantId " +
            "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:isActive IS NULL OR " +
            "     (:isActive = 'all') OR " +
            "     (:isActive = 'true' AND p.isActive = true AND p.category.isActive = true) OR " +
            "     (:isActive = 'false' AND (p.isActive = false OR p.category.isActive = false))) " +
            "AND (:productStatus IS NULL OR " +
            "     (:productStatus = 'all') OR " +
            "     (:productStatus = 'ACTIVE' AND p.stock > p.minimumStock AND p.isActive = true AND p.category.isActive = true) OR " +
            "     (:productStatus = 'LOW_STOCK' AND p.stock > 0 AND p.stock <= p.minimumStock AND p.isActive = true AND p.category.isActive = true) OR " +
            "     (:productStatus = 'OUT_OF_STOCK' AND p.stock = 0 AND p.isActive = true AND p.category.isActive = true) OR " +
            "     (:productStatus = 'DISABLED' AND (p.isActive = false OR p.category.isActive = false)))",
            countQuery = "SELECT COUNT(p) " +
                    "FROM Product p " +
                    "WHERE p.tenantId = :tenantId " +
                    "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                    "AND (:isActive IS NULL OR " +
                    "     (:isActive = 'all') OR " +
                    "     (:isActive = 'true' AND p.isActive = true AND p.category.isActive = true) OR " +
                    "     (:isActive = 'false' AND (p.isActive = false OR p.category.isActive = false))) " +
                    "AND (:productStatus IS NULL OR " +
                    "     (:productStatus = 'all') OR " +
                    "     (:productStatus = 'ACTIVE' AND p.stock > p.minimumStock AND p.isActive = true AND p.category.isActive = true) OR " +
                    "     (:productStatus = 'LOW_STOCK' AND p.stock > 0 AND p.stock <= p.minimumStock AND p.isActive = true AND p.category.isActive = true) OR " +
                    "     (:productStatus = 'OUT_OF_STOCK' AND p.stock = 0 AND p.isActive = true AND p.category.isActive = true) OR " +
                    "     (:productStatus = 'DISABLED' AND (p.isActive = false OR p.category.isActive = false)))")
    Page<Product> findByTenantIdAndNameContainingIgnoreCaseAndActiveAndProductStatus(
            @Param("tenantId") String tenantId,
            @Param("name") String name,
            @Param("isActive") String isActive,
            @Param("productStatus") String productStatus,
            Pageable pageable);
}