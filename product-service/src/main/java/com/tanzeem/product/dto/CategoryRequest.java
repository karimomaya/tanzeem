package com.tanzeem.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank
    private String name;
}
