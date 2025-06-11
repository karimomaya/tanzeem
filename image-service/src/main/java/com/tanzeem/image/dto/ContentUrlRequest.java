package com.tanzeem.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentUrlRequest {
    private String url;
    private Integer order;
    private String title;
    private String description;
}
