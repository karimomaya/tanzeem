package com.tanzeem.image.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentBulkDownloadRequest {
    @NotEmpty(message = "Content URLs list cannot be empty")
    @Valid
    private List<ContentUrlRequest> contentUrls;

    @NotBlank(message = "Context is required")
    private String context;

    @NotBlank(message = "Entity type is required")
    private String entityType;

    private Long entityId;
}
