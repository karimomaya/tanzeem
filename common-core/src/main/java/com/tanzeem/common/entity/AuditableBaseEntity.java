package com.tanzeem.common.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

@MappedSuperclass
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AuditableBaseEntity extends AuditableEntity{
    private String tenantId;
    @ColumnDefault("false")
    private boolean isDeleted = false;
    @ColumnDefault("true")
    private boolean isActive = true;
}
