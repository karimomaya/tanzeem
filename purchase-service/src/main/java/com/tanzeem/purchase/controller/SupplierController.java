package com.tanzeem.purchase.controller;

import com.tanzeem.purchase.dto.SupplierResponse;
import com.tanzeem.purchase.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;
    @GetMapping
    public ResponseEntity<Page<SupplierResponse>> getAllCategories(Pageable pageable, @RequestParam(required = false) String search, @RequestParam(required = false, defaultValue = "true") String isActive ) {
        return ResponseEntity.ok(supplierService.getAll(search, isActive, pageable));
    }

}
