package com.tanzeem.common.client;

import com.tanzeem.common.enums.RatingTargetType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "lookup-service", url = "http://localhost:8088/api")
public interface RatingClient {
    // Define methods to interact with the rating service
    // For example:
    // @PostMapping("/ratings")
    // ResponseEntity<RatingResponse> rate(@RequestBody RatingRequest ratingRequest);

    // @GetMapping("/ratings")
    // ResponseEntity<List<RatingResponse>> list(@RequestParam Long targetId, @RequestParam RatingTargetType targetType);

     @GetMapping("/ratings/average")
    BigDecimal getAverageRating(@RequestParam Long targetId, @RequestParam RatingTargetType targetType);
}
