package com.tanzeem.purchase.entity;

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

@Entity
@Table(name = "suppliers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Supplier {
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
    private String city;
    private String postalCode;
    private Long country;
    private Long governorate;

    // Business Info
    private String businessType;
    private String taxNumber;
    private String registrationNumber;
    private String paymentTerms; // e.g. "net_30"
    private Double creditLimit;

    // Visual Info
    private String icon; // e.g. mdi-truck
    private String color; // hex: #366091

    @Lob
    private String notes;

    private boolean isDeleted = false;
    private String tenantId;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
}
