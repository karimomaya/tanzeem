package com.tanzeem.purchase.entity.lookup;

import com.tanzeem.common.entity.BaseLookupEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "business_types")
@Data
@AllArgsConstructor
public class BusinessType extends BaseLookupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
