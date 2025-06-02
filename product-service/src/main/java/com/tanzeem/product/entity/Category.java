package com.tanzeem.product.entity;

import com.tanzeem.common.entity.AuditableBaseEntity;
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

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
