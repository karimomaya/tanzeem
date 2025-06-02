package com.tanzeem.common.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@Data
public class AuditableBaseEntity extends AuditableEntity{
    private String tenantId;
    private boolean isDeleted = false;
}
