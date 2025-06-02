package com.tanzeem.purchase.service;

import com.tanzeem.purchase.entity.lookup.BusinessType;

import java.util.List;

public interface BusinessTypeService {
    List<BusinessType> getAll();
    BusinessType getById(Long id);
    BusinessType create(BusinessType businessType);
    BusinessType update(Long id, BusinessType updated);
    void delete(Long id);
}
