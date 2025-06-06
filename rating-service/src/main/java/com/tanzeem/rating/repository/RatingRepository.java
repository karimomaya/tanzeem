package com.tanzeem.rating.repository;

import com.tanzeem.rating.entity.Rating;
import com.tanzeem.common.enums.RatingTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByTargetIdAndTargetTypeAndTenantId(Long targetId, RatingTargetType targetType, String tenantId);
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.targetId = :targetId AND r.targetType = :targetType AND r.tenantId = :tenantId")
    Optional<BigDecimal> getAverageRatingByTargetIdAndTargetType(@Param("targetId") Long targetId,
                                                                @Param("targetType") RatingTargetType targetType,
                                                                @Param("tenantId") String tenantId);


    @Query("""
    SELECT AVG(r.score)
    FROM Rating r
    WHERE r.targetType = :targetType
      AND r.createdAt BETWEEN :start AND :end
    """)
    BigDecimal findAverageRatingByTargetTypeAndDateRange(
            @Param("targetType") RatingTargetType targetType,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
