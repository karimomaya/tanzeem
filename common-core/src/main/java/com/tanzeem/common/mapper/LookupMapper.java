package com.tanzeem.common.mapper;

import com.tanzeem.common.dto.LookupResponse;
import com.tanzeem.common.entity.BaseLookupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Builder
@Component
public class LookupMapper {
    public LookupResponse mapToResponse(BaseLookupEntity baseLookupEntity){
        return LookupResponse.builder()
                .name(baseLookupEntity.getName())
                .createdBy(baseLookupEntity.getCreatedBy())
                .createdAt(baseLookupEntity.getCreatedAt())
                .updatedBy(baseLookupEntity.getUpdatedBy())
                .updatedAt(baseLookupEntity.getUpdatedAt())
                .isActive(baseLookupEntity.isActive())
                .code(baseLookupEntity.getCode())
                .description(baseLookupEntity.getDescription())
                .build();
    }
}
