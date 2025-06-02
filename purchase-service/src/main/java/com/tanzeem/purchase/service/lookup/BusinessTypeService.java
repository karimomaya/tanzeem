package com.tanzeem.purchase.service.lookup;

import com.tanzeem.purchase.dto.lookup.BusinessTypeResponse;
import com.tanzeem.purchase.entity.lookup.BusinessType;

import java.util.List;

public interface BusinessTypeService {
    List<BusinessTypeResponse> getAll();
    BusinessType getById(Long id);
    BusinessType create(BusinessType businessType);
    BusinessType update(Long id, BusinessType updated);
    void delete(Long id);
}
