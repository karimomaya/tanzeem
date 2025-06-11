package com.tanzeem.image.dto;


import com.tanzeem.image.enums.ContentCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentResponse {
    private Long id;
    private String filename;
    private String originalName;
    private String url;
    private String contentType;
    private Long size;
    private String context;
    private String entityType;
    private Long entityId;
    private Integer contentOrder;
    private Boolean isPrimary;
    private ContentCategory contentCategory;
    private String title;
    private String description;
    private String alt;
    private Boolean isPublic;
    private Long downloadCount;
    private String fileHash;
    private LocalDateTime createdAt;
}