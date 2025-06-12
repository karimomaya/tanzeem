package com.tanzeem.image.entity;

import com.tanzeem.common.entity.AuditableBaseEntity;
import com.tanzeem.image.enums.ContentCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "contents")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Content extends AuditableBaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String relativePath;

    @Column(nullable = false)
    private String contentType; // MIME type (image/jpeg, application/pdf, etc.)

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String context; // e.g., "product", "category", "user", "document", "general"

    @Column(name = "entity_type")
    private String entityType; // e.g., "Product", "Category", "User", "Order"

    @Column(name = "entity_id")
    private Long entityId; // Reference to the entity

    @Column(name = "content_order")
    private Integer contentOrder; // For ordering multiple files

    @Column(name = "is_primary")
    private Boolean isPrimary = false; // Mark primary content for entities with multiple files

    @Enumerated(EnumType.STRING)
    @Column(name = "content_category")
    private ContentCategory contentCategory; // IMAGE, DOCUMENT, VIDEO, AUDIO, ARCHIVE, OTHER

    private String title; // Display title
    private String description; // Description/caption
    private String alt; // Alt text for accessibility (mainly for images)

    // File-specific metadata (stored as JSON or separate fields)
    private String metadata; // JSON string for additional file-specific data

    @Column(name = "is_public")
    private Boolean isPublic = false; // For public access control

    @Column(name = "download_count")
    private Long downloadCount = 0L; // Track downloads

    @Column(name = "file_hash")
    private String fileHash; // For duplicate detection
}