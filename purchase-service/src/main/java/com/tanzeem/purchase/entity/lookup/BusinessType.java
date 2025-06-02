package com.tanzeem.purchase.entity.lookup;

import com.tanzeem.common.entity.BaseLookupEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "business_types")
@Data
public class BusinessType extends BaseLookupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
