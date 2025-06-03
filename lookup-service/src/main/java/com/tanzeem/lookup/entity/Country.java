package com.tanzeem.lookup.entity;

import com.tanzeem.common.entity.AuditableEntity;
import com.tanzeem.common.entity.BaseLookupEntity;
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
public class Country extends BaseLookupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String flagIcon; // Optional
}
