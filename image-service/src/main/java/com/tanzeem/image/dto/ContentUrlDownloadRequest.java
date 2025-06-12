package com.tanzeem.image.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentUrlDownloadRequest {
    @NotBlank(message = "Content URL is required")
    private String contentUrl;

    @NotBlank(message = "Context is required")
    private String context;

    @NotBlank(message = "Entity type is required")
    private String entityType;

    private Long entityId;
    private Boolean isPrimary;
    private Integer order;
    private String title;
    private String description;
}
