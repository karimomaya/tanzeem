package com.tanzeem.lookup.entity;

import com.tanzeem.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "countries")
public class Country extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private String code; // Optional: eg. EG, SA
    private String flagIcon; // Optional
    @ColumnDefault("false")
    private boolean isDeleted = false;
    @ColumnDefault("true")
    private boolean isActive = true;
}
