package com.tanzeem.purchase.service.impl;

import com.tanzeem.common.client.RatingClient;
import com.tanzeem.common.enums.RatingTargetType;
import com.tanzeem.purchase.dto.SupplierRequest;
import com.tanzeem.purchase.dto.SupplierResponse;
import com.tanzeem.purchase.entity.Supplier;
import com.tanzeem.purchase.mapper.SupplierMapper;
import com.tanzeem.purchase.repository.SupplierRepository;
import com.tanzeem.purchase.repository.lookup.BusinessTypeRepository;
import com.tanzeem.purchase.repository.lookup.PaymentTermRepository;
import com.tanzeem.purchase.service.PurchaseService;
import com.tanzeem.purchase.service.SupplierService;
import com.tanzeem.security.common.AuthContextHolder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final PurchaseService purchaseService;
    private final RatingClient ratingClient;
    private final BusinessTypeRepository businessTypeRepository;
    private final PaymentTermRepository paymentTermRepository;
    @Override
    public Page<SupplierResponse> getAll(String search, String isActive, Pageable pageable) {
        if (search == null) search = "";
        Boolean isActiveParam = "all".equalsIgnoreCase(isActive) ? null : Boolean.valueOf(isActive);
        Page<Supplier> suppliers;
        if (isActiveParam == null) suppliers = supplierRepository.findByTenantIdAndNameContainingIgnoreCase(AuthContextHolder.getTenantId(),search , pageable);
        else suppliers = supplierRepository.findByIsActiveAndTenantIdAndNameContainingIgnoreCase(isActiveParam, AuthContextHolder.getTenantId(),search , pageable);

        return suppliers.map(supplierMapper::mapToResponse);
    }

    @Override
    public SupplierResponse update(Long id, SupplierRequest supplierRequest) {
        Supplier existingSupplier = supplierRepository.findByIdAndTenantId(id, AuthContextHolder.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + id));


        businessTypeRepository.findByCodeAndTenantId(supplierRequest.getBusinessType(), AuthContextHolder.getTenantId())
                .ifPresent(existingSupplier::setBusinessType);
        paymentTermRepository.findByCodeAndTenantId(supplierRequest.getPaymentTerm(), AuthContextHolder.getTenantId())
                .ifPresent(existingSupplier::setPaymentTerm);

        existingSupplier.setName(supplierRequest.getName());
        existingSupplier.setCode(supplierRequest.getCode());
        existingSupplier.setContactPerson(supplierRequest.getContactPerson());
        existingSupplier.setPhone(supplierRequest.getPhone());
        existingSupplier.setEmail(supplierRequest.getEmail());
        existingSupplier.setIsActive(supplierRequest.getIsActive());
        existingSupplier.setAddress(supplierRequest.getAddress());
        existingSupplier.setPostalCode(supplierRequest.getPostalCode());
        existingSupplier.setCountryCode(supplierRequest.getCountry());
        existingSupplier.setGovernorateCode(supplierRequest.getGovernorate());
        existingSupplier.setTaxNumber(supplierRequest.getTaxNumber());
        existingSupplier.setRegistrationNumber(supplierRequest.getRegistrationNumber());
        existingSupplier.setCreditLimit(supplierRequest.getCreditLimit());
        existingSupplier.setIcon(supplierRequest.getIcon());
        existingSupplier.setColor(supplierRequest.getColor());
        existingSupplier.setNotes(supplierRequest.getNotes());

        // TODO: Calculate Rating

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return supplierMapper.mapToResponse(updatedSupplier);
    }

    @Override
    public void delete(Long id) {
        supplierRepository.deleteById(id);
    }

    /**
     * How to rate supplier
        نسبة التوريد في الوقت (OnTimeDeliveryRate)	40%	عدد الطلبات اللي تم تسليمها في وقتها ÷ إجمالي الطلبات
        دقة التوريد (Accuracy)	25%	(إجمالي الطلبات - المرتجعات) ÷ إجمالي الطلبات
        سرعة الرد (AvgResponseTime)	10%	كل ما المورد يرد بسرعة على الطلب، بياخد نقاط أكتر
        عدد الطلبات الكلي (Order Volume)	10%	مورد عليه ضغط شغل أكتر = نقاط أعلى
        تقييم يدوي (Manual Rating)	15%	ممكن تدخل تقييم يدوي للمورد (مثلاً من 10)
     **/

    private int calculateSupplierRating(Supplier supplier) {
        Long supplierId = supplier.getId();
        // On-Time Delivery Score
        int totalOrders = purchaseService.countBySupplierId(supplierId);
        if (totalOrders == 0) return 0;

        long onTimeDeliveries = purchaseService.countOnTimeDeliveries(supplierId);
        double onTimeDeliveryRate = (onTimeDeliveries * 1.0) / totalOrders;

        //@TODO:implement purchase return
        int returnedOrders = 0;//purchaseReturnRepository.countBySupplierIdAndTenantId(supplierId, tenantId); // You need this repo

        double avgResponseTime = (purchaseService.getAverageResponseTimeInDays(supplier.getId()));


        BigDecimal manualRating = ratingClient.getAverageRating(supplierId, RatingTargetType.SUPPLIER);
        double manualScore = manualRating != null ? manualRating.doubleValue() : 0.0;

        double onTimeScore = onTimeDeliveryRate * 40;
        double accuracyScore = ((double)(totalOrders - returnedOrders) / totalOrders) * 25;


        double responseScore = 0;
        if (avgResponseTime < 1) responseScore = 10;
        else if (avgResponseTime < 3) responseScore = 5;

        double orderVolumeScore = 0;
        if (totalOrders > 50) orderVolumeScore = 10;
        else if (totalOrders > 20) orderVolumeScore = 5;

        double totalScore = onTimeScore + accuracyScore + responseScore + manualScore + orderVolumeScore;

        return Math.min((int) Math.round(totalScore), 100);
    }
}
