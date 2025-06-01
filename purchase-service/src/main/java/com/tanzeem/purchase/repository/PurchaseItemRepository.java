package com.tanzeem.purchase.repository;

import com.tanzeem.purchase.entity.PurchaseItem;
import com.tanzeem.purchase.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {
}
