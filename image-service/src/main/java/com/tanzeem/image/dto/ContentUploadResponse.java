package com.tanzeem.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentUploadResponse {
    private String url;
    private String filename;
    private String originalName;
    private String relativePath;
    private Long contentId;
    private String contentCategory;
}
