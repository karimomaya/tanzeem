package com.tanzeem.purchase.entity.lookup;

import com.tanzeem.common.entity.BaseLookupEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_terms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTerm extends BaseLookupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
