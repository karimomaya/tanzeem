package com.tanzeem.product.entity;

import com.tanzeem.common.entity.AuditableBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Category extends AuditableBaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String icon;
    private String description;
    private String color;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;


}
