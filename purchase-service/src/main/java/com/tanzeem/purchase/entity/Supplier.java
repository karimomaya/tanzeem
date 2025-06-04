package com.tanzeem.purchase.entity;

import com.tanzeem.common.entity.AuditableBaseEntity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
@SQLDelete(sql = "UPDATE suppliers SET deleted = true WHERE id=?")
@SQLRestriction("is_deleted=false")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Supplier extends AuditableBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Info
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String code;

    private String contactPerson;
    @Column(nullable = false)
    private String phone;
    private String email;
    private Boolean isActive = true;

    // Address Info
    private String address;
    private String postalCode;
    private String countryCode;
    private String governorateCode;

    // Business Info
    private String businessType;
    private String taxNumber;
    private String registrationNumber;
    private String paymentTerms; // e.g. "net_30"
    private Double creditLimit;

    // Visual Info
    private String icon; // e.g. mdi-truck
    private String color; // hex: #366091

    private Integer rating;

    @Lob
    private String notes;

}
