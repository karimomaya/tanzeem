package com.tanzeem.purchase.repository;

import com.tanzeem.purchase.entity.Purchase;
import com.tanzeem.purchase.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
