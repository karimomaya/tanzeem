package com.tanzeem.common.client;

import com.tanzeem.common.enums.RatingTargetType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "lookup-service", url = "http://localhost:8088/api")
public interface RatingClient {
     @GetMapping("/ratings/average")
    BigDecimal getAverageRating(@RequestParam Long targetId, @RequestParam RatingTargetType targetType);
    @GetMapping("/ratings/average/all")
    BigDecimal getAverageRatingForAll(@RequestParam("targetType") RatingTargetType targetType, @RequestParam("start") String start,@RequestParam("end") String end);
}
