package com.tanzeem.purchase.controller;

import com.tanzeem.purchase.dto.lookup.BusinessTypeResponse;
import com.tanzeem.purchase.entity.lookup.BusinessType;
import com.tanzeem.purchase.service.lookup.BusinessTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business-types")
@RequiredArgsConstructor
public class BusinessTypeController {
    private final BusinessTypeService service;

    @GetMapping
    public List<BusinessTypeResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public BusinessType getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public BusinessType create(@RequestBody BusinessType businessType) {
        return service.create(businessType);
    }

    @PutMapping("/{id}")
    public BusinessType update(@PathVariable Long id, @RequestBody BusinessType updated) {
        return service.update(id, updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
