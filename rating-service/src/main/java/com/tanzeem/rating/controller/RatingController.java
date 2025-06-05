package com.tanzeem.rating.controller;

import com.tanzeem.rating.dto.RatingRequest;
import com.tanzeem.rating.dto.RatingResponse;
import com.tanzeem.common.enums.RatingTargetType;
import com.tanzeem.rating.service.RatingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingResponse> rate(@RequestBody RatingRequest ratingRequest) {
        RatingResponse ratingResponse =  ratingService.createRating(ratingRequest);
        return ResponseEntity.ok(ratingResponse);
    }

    @GetMapping
    public ResponseEntity<List<RatingResponse>> list(@RequestParam Long targetId, @RequestParam RatingTargetType targetType ) {
        List<RatingResponse> ratingResponses = ratingService.getRatings(targetId, targetType);
        return ResponseEntity.ok(ratingResponses);
    }

    @GetMapping("/average")
    public ResponseEntity<BigDecimal> average(@RequestParam Long targetId, @RequestParam RatingTargetType targetType) {
        BigDecimal bigDecimal =  ratingService.getAverageRating(targetId, targetType);
        return ResponseEntity.ok(bigDecimal);
    }
}
