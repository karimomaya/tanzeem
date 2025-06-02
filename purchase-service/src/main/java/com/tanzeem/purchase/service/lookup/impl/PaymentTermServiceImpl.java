package com.tanzeem.purchase.service.lookup.impl;

import com.tanzeem.purchase.dto.lookup.PaymentTermResponse;
import com.tanzeem.purchase.entity.lookup.PaymentTerm;
import com.tanzeem.purchase.repository.lookup.PaymentTermRepository;
import com.tanzeem.purchase.service.lookup.PaymentTermService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentTermServiceImpl implements PaymentTermService {
    private final PaymentTermRepository repository;

    @Override
    @Cacheable(value = "paymentTerm", key = "T(com.tanzeem.security.common.AuthContextHolder).getTenantId()")
    public List<PaymentTermResponse> getByTenant(String tenantId) {
        return repository.findByTenantIdOrderByNameAsc(tenantId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @CacheEvict(value = "paymentTerm", key = "T(com.tanzeem.security.common.AuthContextHolder).getTenantId()")
    public PaymentTermResponse create(PaymentTerm paymentTerm) {
        PaymentTerm term = repository.save(paymentTerm);
        return mapToResponse(term);
    }

    @Override
    @CacheEvict(value = "paymentTerm", key = "T(com.tanzeem.security.common.AuthContextHolder).getTenantId()")
    public PaymentTermResponse update(Long id, PaymentTerm paymentTerm) {
        PaymentTerm term =  repository.save(paymentTerm);
        return mapToResponse(term);
    }

    @Override
    @CacheEvict(value = "paymentTerm", key = "T(com.tanzeem.security.common.AuthContextHolder).getTenantId()")
    public void delete(Long id) {
        PaymentTerm entity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("PaymentTerm not found"));
        repository.delete(entity);
    }

    private PaymentTermResponse mapToResponse(PaymentTerm paymentTerm) {
        return new PaymentTermResponse(
                paymentTerm.getId(),
                paymentTerm.getCode(),
                paymentTerm.getName(),
                paymentTerm.getDescription(),
                paymentTerm.isActive(),
                paymentTerm.isDeleted()
        );
    }
}
