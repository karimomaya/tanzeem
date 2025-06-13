package com.tanzeem.product.entity;

import com.tanzeem.common.entity.AuditableBaseEntity;
import com.tanzeem.product.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
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

    @ManyToOne
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status = ProductStatus.ACTIVE;
}
