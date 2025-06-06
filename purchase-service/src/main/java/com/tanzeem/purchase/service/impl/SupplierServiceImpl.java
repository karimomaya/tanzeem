package com.tanzeem.purchase.service.impl;

import com.tanzeem.common.client.RatingClient;
import com.tanzeem.common.enums.RatingTargetType;
import com.tanzeem.purchase.dto.SupplierRequest;
import com.tanzeem.purchase.dto.SupplierResponse;
import com.tanzeem.purchase.dto.SupplierStatsResponse;
import com.tanzeem.purchase.dto.TopSupplierResponse;
import com.tanzeem.purchase.entity.Supplier;
import com.tanzeem.purchase.mapper.SupplierMapper;
import com.tanzeem.purchase.repository.PurchaseRepository;
import com.tanzeem.purchase.repository.SupplierRepository;
import com.tanzeem.purchase.repository.lookup.BusinessTypeRepository;
import com.tanzeem.purchase.repository.lookup.PaymentTermRepository;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import com.tanzeem.purchase.service.SupplierService;
import com.tanzeem.security.common.AuthContextHolder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@AllArgsConstructor
@Service
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final PurchaseRepository purchaseRepository;
    private final RatingClient ratingClient;
    private final BusinessTypeRepository businessTypeRepository;
    private final PaymentTermRepository paymentTermRepository;


    @Override
    public SupplierResponse create(SupplierRequest supplierRequest) {
        Supplier supplier = supplierMapper.mapToEntity(supplierRequest);
        supplier.setTenantId(AuthContextHolder.getTenantId());

        try {businessTypeRepository.findByCodeAndTenantId(supplierRequest.getBusinessType(), AuthContextHolder.getTenantId())
                .ifPresent(supplier::setBusinessType);} catch (Exception ex) {}
        try {paymentTermRepository.findByCodeAndTenantId(supplierRequest.getPaymentTerm(), AuthContextHolder.getTenantId())
                .ifPresent(supplier::setPaymentTerm);} catch (Exception ex) {}

        // Calculate initial rating
        int rating = calculateSupplierRating(supplier);
        supplier.setRating(rating);

        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.mapToResponse(savedSupplier);
    }

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

    @Override
    public SupplierStatsResponse getSupplierStats() {
        String tenantId = AuthContextHolder.getTenantId();

        // Current period
        long total = supplierRepository.countByTenantId(tenantId);
        long active = supplierRepository.countByIsActiveTrueAndTenantId(tenantId);

        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

        long newSuppliersThisMonth = supplierRepository.countByCreatedAtBetweenAndTenantId(startOfMonth, endOfMonth, tenantId);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


        String startOfMonthStr = startOfMonth.format(formatter);
        String nowStr = LocalDateTime.now().format(formatter);

        BigDecimal avgRating = ratingClient.getAverageRatingForAll(RatingTargetType.SUPPLIER, startOfMonthStr, nowStr);

        BigDecimal totalSpending = Optional.ofNullable(purchaseRepository.sumTotalAmountByTenantId(tenantId))
                .orElse(BigDecimal.ZERO);
        BigDecimal avgOrderValue = Optional.ofNullable(purchaseRepository.avgTotalAmountByTenantId(tenantId))
                .orElse(BigDecimal.ZERO);

        List<Object[]> topSuppliers = purchaseRepository.findTopSupplierByTotalAmount(tenantId);
        TopSupplierResponse topSupplier = topSuppliers.isEmpty() ? null : new TopSupplierResponse(
                (String) topSuppliers.get(0)[1], // Name
                ((Number) topSuppliers.get(0)[2]).intValue(), // Order Count
                (BigDecimal) topSuppliers.get(0)[3] // Total Amount
        );

        long onTime = purchaseRepository.countOnTimeDeliveries(tenantId);
        long totalOrders = purchaseRepository.countByTenantId(tenantId);
        double onTimeDeliveryRate = totalOrders > 0 ? (onTime * 1.0 / totalOrders) * 100 : 0;

        // Previous period
        YearMonth previousMonth = currentMonth.minusMonths(1);
        LocalDateTime startOfPreviousMonth = previousMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfPreviousMonth = previousMonth.atEndOfMonth().atTime(LocalTime.MAX);

        long previousNewSuppliers = supplierRepository.countByCreatedAtBetweenAndTenantId(startOfPreviousMonth, endOfPreviousMonth, tenantId);



        String startOfPreviousMonthStr = startOfPreviousMonth.format(formatter);
        String endOfPreviousMonthStr = endOfPreviousMonth.format(formatter);


        BigDecimal previousAvgRating = ratingClient.getAverageRatingForAll(RatingTargetType.SUPPLIER, startOfPreviousMonthStr, endOfPreviousMonthStr);
        BigDecimal previousTotalSpending = Optional.ofNullable(purchaseRepository.sumTotalAmountByTenantIdAndCreatedAtBetween(tenantId, startOfPreviousMonth, endOfPreviousMonth))
                .orElse(BigDecimal.ZERO);
        BigDecimal previousAvgOrderValue = Optional.ofNullable(purchaseRepository.avgTotalAmountByTenantIdAndCreatedAtBetween(tenantId, startOfPreviousMonth, endOfPreviousMonth))
                .orElse(BigDecimal.ZERO);

        long previousOnTime = purchaseRepository.countOnTimeDeliveries(tenantId); // Ideally you'd have a time filter here too
        long previousTotalOrders = purchaseRepository.countByTenantId(tenantId);  // Ideally you'd count for previous month
        double previousOnTimeDeliveryRate = previousTotalOrders > 0 ? (previousOnTime * 1.0 / previousTotalOrders) * 100 : 0;

        return SupplierStatsResponse.builder()
                .total(total)
                .active(active)
                .newThisMonth(newSuppliersThisMonth)
                .averageRating(avgRating)
                .totalSpending(totalSpending)
                .averageOrderValue(avgOrderValue)
                .topSupplier(topSupplier)
                .onTimeDeliveryRate(onTimeDeliveryRate)

                .totalGrowth(calculateGrowth(total, total)) // total is not filtered by time, so growth might not be meaningful
                .newSuppliersGrowth(calculateGrowth(newSuppliersThisMonth, previousNewSuppliers))
                .ratingTrend(calculateGrowth(avgRating, previousAvgRating))
                .spendingGrowth(calculateGrowth(totalSpending, previousTotalSpending))
                .averageOrderTrend(calculateGrowth(avgOrderValue, previousAvgOrderValue))
                .deliveryTrend(calculateGrowth(onTimeDeliveryRate, previousOnTimeDeliveryRate))

                .build();
    }


    private double calculateGrowth(double current, double previous) {
        if (previous == 0) return 0;
        return ((current - previous) / previous) * 100;
    }

    private BigDecimal calculateGrowth(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        BigDecimal diff = current.subtract(previous);
        return diff.multiply(BigDecimal.valueOf(100)).divide(previous, 1, RoundingMode.HALF_UP);
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
        int totalOrders = purchaseRepository.countBySupplierIdAndTenantId(supplierId, AuthContextHolder.getTenantId());
        if (totalOrders == 0) return 0;

        long onTimeDeliveries = purchaseRepository.countOnTimeDeliveriesBySupplierId(supplierId, AuthContextHolder.getTenantId());
        double onTimeDeliveryRate = (onTimeDeliveries * 1.0) / totalOrders;

        //@TODO:implement purchase return
        int returnedOrders = 0;//purchaseReturnRepository.countBySupplierIdAndTenantId(supplierId, tenantId); // You need this repo

        double avgResponseTime = (purchaseRepository.getAverageResponseTimeInDays(supplier.getId(), AuthContextHolder.getTenantId())
                .orElse(0.0) / 30.0); // Convert to months


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
