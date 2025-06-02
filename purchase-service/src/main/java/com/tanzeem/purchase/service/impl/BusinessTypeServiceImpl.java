package com.tanzeem.purchase.service.impl;

import com.tanzeem.purchase.entity.lookup.BusinessType;
import com.tanzeem.purchase.repository.BusinessTypeRepository;
import com.tanzeem.purchase.service.BusinessTypeService;
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
    public List<BusinessType> getAll() {
        return repository.findByTenantIdByOrderByNameAsc(AuthContextHolder.getTenantId());
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
