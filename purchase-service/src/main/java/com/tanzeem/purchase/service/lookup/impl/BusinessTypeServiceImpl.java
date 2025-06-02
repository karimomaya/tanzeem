package com.tanzeem.purchase.service.lookup.impl;

import com.tanzeem.purchase.dto.lookup.BusinessTypeResponse;
import com.tanzeem.purchase.entity.lookup.BusinessType;
import com.tanzeem.purchase.repository.lookup.BusinessTypeRepository;
import com.tanzeem.purchase.service.lookup.BusinessTypeService;
import com.tanzeem.security.common.AuthContextHolder;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BusinessTypeServiceImpl implements BusinessTypeService {
    private final BusinessTypeRepository repository;

    @Override
    @Cacheable(value = "businessType", key = "T(com.tanzeem.security.common.AuthContextHolder).getTenantId()")
    public List<BusinessTypeResponse> getAll() {
        List<BusinessType> businessTypes =  repository.findByTenantIdOrderByNameAsc(AuthContextHolder.getTenantId());
        return businessTypes.stream()
                .map(b -> new BusinessTypeResponse(b.getId(), b.getCode(), b.getName(), b.getDescription(), b.isActive(), b.isDeleted()))
                .toList();
    }

    @Override
    public BusinessType getById(Long id) {
        return repository.findByIdAndTenantId(id, AuthContextHolder.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("BusinessType not found"));
    }

    @Override
    @CacheEvict(value = "businessType", key = "T(com.tanzeem.security.common.AuthContextHolder).getTenantId()")
    public BusinessType create(BusinessType businessType) {
        businessType.setTenantId(AuthContextHolder.getTenantId());
        return repository.save(businessType);
    }

    @Override
    @CacheEvict(value = "businessType", key = "T(com.tanzeem.security.common.AuthContextHolder).getTenantId()")
    public BusinessType update(Long id, BusinessType updated) {
        BusinessType existing = getById(id);
        existing.setCode(updated.getCode());
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return repository.save(existing);
    }

    @Override
    @CacheEvict(value = "businessType", key = "T(com.tanzeem.security.common.AuthContextHolder).getTenantId()")
    public void delete(Long id) {
        BusinessType existing = getById(id);
        repository.delete(existing);
    }

}
