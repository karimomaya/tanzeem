package com.tanzeem.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
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

    private String tenantId;
}
