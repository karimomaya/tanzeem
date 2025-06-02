package com.tanzeem.product.entity;

import com.tanzeem.common.entity.AuditableBaseEntity;
import com.tanzeem.product.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product  extends AuditableBaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String sku;
    private String barcode;
    private String unit;

    private BigDecimal price;
    private Integer stock;

    private Integer minimumStock;
    private String imageUrl;

    @ManyToOne
    private Category category;

    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status = ProductStatus.ACTIVE;
}
