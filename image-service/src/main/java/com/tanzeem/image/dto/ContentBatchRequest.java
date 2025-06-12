package com.tanzeem.image.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentBatchRequest {

    @NotBlank(message = "Entity type is required")
    private String entityType;
    @NotEmpty(message = "Entity IDs list cannot be empty")
    private List<Long> entityIds;
    private String contentCategory;
}
