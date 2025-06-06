package com.tanzeem.purchase.controller;

import com.tanzeem.purchase.dto.SupplierRequest;
import com.tanzeem.purchase.dto.SupplierResponse;
import com.tanzeem.purchase.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;
    @GetMapping
    public ResponseEntity<Page<SupplierResponse>> getAllCategories(Pageable pageable, @RequestParam(required = false) String search, @RequestParam(required = false, defaultValue = "true") String isActive ) {
        return ResponseEntity.ok(supplierService.getAll(search, isActive, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> update(@PathVariable Long id, @RequestBody SupplierRequest supplierRequest) {
        return ResponseEntity.ok(supplierService.update(id, supplierRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
