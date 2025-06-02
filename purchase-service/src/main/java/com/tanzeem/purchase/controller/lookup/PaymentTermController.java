package com.tanzeem.purchase.controller.lookup;

import com.tanzeem.purchase.dto.lookup.PaymentTermResponse;
import com.tanzeem.purchase.entity.lookup.PaymentTerm;
import com.tanzeem.purchase.service.lookup.PaymentTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-terms")
@RequiredArgsConstructor
public class PaymentTermController {
    private final PaymentTermService service;

    @GetMapping
    public List<PaymentTermResponse> getAll() {
        return service.getAll();
    }

    @PostMapping
    public PaymentTermResponse create(@RequestBody PaymentTerm paymentTerm) {
        return service.create(paymentTerm);
    }

    @PutMapping("/{id}")
    public PaymentTermResponse update(@PathVariable Long id, @RequestBody PaymentTerm paymentTerm) {
        return service.update(id, paymentTerm);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
