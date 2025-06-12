package com.tanzeem.image.repository;

import com.tanzeem.image.entity.Content;
import com.tanzeem.image.enums.ContentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  ContentRepository  extends JpaRepository<Content, Long> {
    List<Content> findByTenantIdAndEntityTypeAndEntityIdOrderByContentOrderAsc(
            String tenantId, String entityType, Long entityId);

    List<Content> findByTenantIdAndEntityTypeAndEntityIdAndContentCategoryOrderByContentOrderAsc(
            String tenantId, String entityType, Long entityId, ContentCategory contentCategory);

    List<Content> findByTenantIdAndContextOrderByCreatedAtDesc(
            String tenantId, String context);

    List<Content> findByTenantIdAndContentCategoryOrderByCreatedAtDesc(
            String tenantId, ContentCategory contentCategory);

    List<Content> findByTenantIdAndUrl(String tenantId, String url);

    Optional<Content> findByTenantIdAndEntityTypeAndEntityIdAndIsPrimary(
            String tenantId, String entityType, Long entityId, Boolean isPrimary);

    @Query("SELECT c FROM Content c WHERE c.tenantId = :tenantId AND c.entityType = :entityType AND c.entityId IN :entityIds")
    List<Content> findByTenantIdAndEntityTypeAndEntityIdIn(
            @Param("tenantId") String tenantId,
            @Param("entityType") String entityType,
            @Param("entityIds") List<Long> entityIds);

    @Query("SELECT c FROM Content c WHERE c.tenantId = :tenantId AND c.entityType = :entityType AND c.entityId IN :entityIds AND c.contentCategory = :category")
    List<Content> findByTenantIdAndEntityTypeAndEntityIdInAndContentCategory(
            @Param("tenantId") String tenantId,
            @Param("entityType") String entityType,
            @Param("entityIds") List<Long> entityIds,
            @Param("category") ContentCategory category);

    void deleteByTenantIdAndEntityTypeAndEntityId(String tenantId, String entityType, Long entityId);

    Optional<Content> findByTenantIdAndFileHash(String tenantId, String fileHash);

    @Modifying
    @Query("UPDATE Content c SET c.downloadCount = c.downloadCount + 1 WHERE c.id = :contentId")
    void incrementDownloadCount(@Param("contentId") Long contentId);

    @Query("SELECT COUNT(c) FROM Content c WHERE c.tenantId = :tenantId AND c.contentCategory = :category")
    Long countByTenantIdAndContentCategory(@Param("tenantId") String tenantId, @Param("category") ContentCategory category);

    @Query("SELECT SUM(c.size) FROM Content c WHERE c.tenantId = :tenantId")
    Long getTotalStorageUsed(@Param("tenantId") String tenantId);



}
