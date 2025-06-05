package com.tanzeem.rating.service;

import com.tanzeem.rating.dto.RatingRequest;
import com.tanzeem.rating.dto.RatingResponse;
import com.tanzeem.common.enums.RatingTargetType;

import java.math.BigDecimal;
import java.util.List;

public interface RatingService {
    RatingResponse createRating(RatingRequest ratingRequest);
    List<RatingResponse> getRatings(Long targetId, RatingTargetType targetType);
    BigDecimal getAverageRating(Long targetId, RatingTargetType targetType);
}
