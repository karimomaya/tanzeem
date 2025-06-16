package com.tanzeem.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tanzeem.common.entity.AuditableBaseEntity;
import com.tanzeem.product.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Product  extends AuditableBaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String sku;
    private String barcode;
    private String unit;

    private BigDecimal price;
    private Integer stock;

    private Integer minimumStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("products")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status = ProductStatus.ACTIVE;
}
