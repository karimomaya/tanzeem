package com.tanzeem.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageUploadResponse {
    private String url;
    private String filename;
    private String originalName;
    private String relativePath;


}
