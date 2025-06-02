package com.tanzeem.purchase.service.lookup;

import com.tanzeem.purchase.dto.lookup.PaymentTermResponse;
import com.tanzeem.purchase.entity.lookup.PaymentTerm;

import java.util.List;

public interface PaymentTermService {
    List<PaymentTermResponse> getAll();
    PaymentTermResponse create(PaymentTerm paymentTerm);
    PaymentTermResponse update(Long id, PaymentTerm paymentTerm);
    void delete(Long id);
}
