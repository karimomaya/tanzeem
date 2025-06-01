package com.tanzeem.iam.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TenantSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessType businessType;

    @Column(nullable = false)
    private String defaultCurrency;

    @Column(nullable = false)
    private String defaultLanguage;

    @Column(nullable = false)
    private String timezone;

    private String tenantId;

    public enum BusinessType {
        PRODUCT,
        SERVICE
    }
}
