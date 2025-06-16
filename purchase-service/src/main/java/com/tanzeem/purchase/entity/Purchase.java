package com.tanzeem.purchase.entity;

import com.tanzeem.common.entity.AuditableBaseEntity;
import com.tanzeem.purchase.enums.PurchaseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchases", indexes = {
        @Index(name = "idx_supplier_id", columnList = "supplier_id"),
        @Index(name = "idx_purchase_date", columnList = "purchaseDate")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class Purchase  extends AuditableBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private LocalDate purchaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private String invoiceNumber;
    @DecimalMin("0.00")
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> items = new ArrayList<>();
    @Lob
    private String notes;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;

    private LocalDateTime expectedDeliveryAt;
    private LocalDateTime deliveredAt;

    private LocalDate confirmedAt;

}
