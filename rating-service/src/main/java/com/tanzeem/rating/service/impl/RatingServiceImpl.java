package com.tanzeem.rating.service.impl;

import com.tanzeem.rating.dto.RatingRequest;
import com.tanzeem.rating.dto.RatingResponse;
import com.tanzeem.rating.entity.Rating;
import com.tanzeem.common.enums.RatingTargetType;
import com.tanzeem.rating.repository.RatingRepository;
import com.tanzeem.rating.service.RatingService;
import com.tanzeem.security.common.AuthContextHolder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class RatingServiceImpl implements RatingService {

    // Inject the RatingRepository
     private final RatingRepository ratingRepository;

    public RatingResponse createRating(RatingRequest ratingRequest) {
        Rating rating = Rating.builder()
                .targetId(ratingRequest.getTargetId())
                .targetType(RatingTargetType.valueOf(ratingRequest.getTargetType()))
                .score(ratingRequest.getRatingValue())
                .reason(ratingRequest.getReason())
                .build();
        return mapToResponse(ratingRepository.save(rating));
    }

    public List<RatingResponse> getRatings(Long targetId, RatingTargetType targetType) {
        return ratingRepository.findByTargetIdAndTargetTypeAndTenantId(targetId, targetType, AuthContextHolder.getTenantId())
                .stream().map(this::mapToResponse).toList();
    }

    public BigDecimal getAverageRating(Long targetId, RatingTargetType targetType) {
        return ratingRepository.getAverageRatingByTargetIdAndTargetType(targetId, targetType, AuthContextHolder.getTenantId())
                .orElse(BigDecimal.ZERO);
    }

    private RatingResponse mapToResponse(Rating rating){
        return RatingResponse.builder().id(rating.getId())
                .score(rating.getScore())
                .reason(rating.getReason())
                .targetId(rating.getTargetId())
                .targetType(rating.getTargetType().name())
                .build();
    }
}
