package com.tanzeem.rating.entity;

import com.tanzeem.common.entity.AuditableBaseEntity;
import com.tanzeem.common.enums.RatingTargetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "ratings")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rating extends AuditableBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tenantId;

    @Enumerated(EnumType.STRING)
    private RatingTargetType targetType;; // e.g. "Supplier", "Product", etc.
    private Long targetId;     // ID of the supplier/product being rated

    private Integer score;     // 0 - 100 or 1 - 5 scale
    private String reason;     // optional note or reason
    private String source;     // "system", "admin", "user", etc.

}
