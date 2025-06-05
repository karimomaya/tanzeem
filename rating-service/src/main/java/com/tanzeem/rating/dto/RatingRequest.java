package com.tanzeem.rating.dto;

import lombok.Data;

@Data
public class RatingRequest {
    private Long targetId;
    private String targetType;
    private Integer ratingValue;
    private String reason;
}
