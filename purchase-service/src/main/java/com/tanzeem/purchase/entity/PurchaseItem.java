package com.tanzeem.purchase.entity;

import com.tanzeem.common.entity.AuditableBaseEntity;
import com.tanzeem.purchase.enums.ValueType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.RoundingMode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;


@Entity
@Table(name = "purchase_items", indexes = {
        @Index(name = "idx_product_id", columnList = "productId"),
        @Index(name = "idx_purchase_id", columnList = "purchase_id")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PurchaseItem  extends AuditableBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long productId;

    private String productNameAtPurchase; // Snapshot at time of purchase (optional but recommended)
    @NotNull
    @Min(1)
    private Integer quantity;
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal unitPrice;
    @DecimalMin("0.00")
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    private BigDecimal discount;
    private BigDecimal tax;

    @Enumerated(EnumType.STRING)
    private ValueType discountType;

    @Enumerated(EnumType.STRING)
    private ValueType taxType;


    public void updateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            BigDecimal base = unitPrice.multiply(BigDecimal.valueOf(quantity));

            // حساب الخصم
            BigDecimal discountAmount = BigDecimal.ZERO;
            if (discount != null && discountType != null) {
                if (discountType == ValueType.PERCENTAGE) {
                    discountAmount = base.multiply(discount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                } else {
                    discountAmount = discount;
                }
            }

            // حساب الضريبة
            BigDecimal taxAmount = BigDecimal.ZERO;
            if (tax != null && taxType != null) {
                if (taxType == ValueType.PERCENTAGE) {
                    taxAmount = base.multiply(tax).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                } else {
                    taxAmount = tax;
                }
            }

            // السعر النهائي
            this.totalPrice = base.subtract(discountAmount).add(taxAmount);
        }
    }

    @PrePersist
    @PreUpdate
    private void preCalculate() {
        updateTotalPrice();
    }
}
