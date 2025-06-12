package com.tanzeem.image.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentUrlRequest {
    @NotBlank(message = "URL is required")
    @Pattern(regexp = "^https?://.*", message = "URL must be a valid HTTP or HTTPS URL")
    private String url;
    private Integer order;
    private String title;
    private String description;
}
