package com.tanzeem.product.service.impl;

import com.tanzeem.product.dto.CategoryProductCountResponse;
import com.tanzeem.product.dto.ProductDto;
import com.tanzeem.product.dto.ProductResponse;
import com.tanzeem.product.entity.Category;
import com.tanzeem.product.entity.Product;
import com.tanzeem.product.enums.ProductStatus;
import com.tanzeem.product.mapper.CategoryMapper;
import com.tanzeem.product.repository.CategoryRepository;
import com.tanzeem.product.repository.ProductRepository;
import com.tanzeem.product.service.ProductService;
import com.tanzeem.security.common.AuthContextHolder;
import lombok.RequiredArgsConstructor;
import org.hibernate.LazyInitializationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final CategoryMapper categoryMapper;

    @Transactional
    @Override
    public ProductResponse create(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setBarcode(dto.getBarcode());
        product.setUnit(dto.getUnit());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setMinimumStock(dto.getMinimumStock());
        product.setTenantId(AuthContextHolder.getTenantId());

        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);

        product.setStatus(calculateProductStatus(product));

        product=  productRepo.save(product);
        return mapToResponse(product);
    }

    public ProductResponse getById(Long id){
        return mapToResponse(Objects.requireNonNull(productRepo.findById(id).orElse(null)));
    }
    @Transactional
    @Override
    public Page<ProductResponse> getAll(String search, String isActive, String productStatus, Pageable pageable) {
        if (search == null) search = "";
        Page<Product> products = productRepo.findByTenantIdAndNameContainingIgnoreCaseAndActiveAndProductStatus(
                AuthContextHolder.getTenantId(), search, isActive, productStatus,  pageable);
        return products.map(this::mapToResponse);
    }

    @Override
    public Product update(Long id, ProductDto dto) {
        Product product = productRepo.findById(id).filter(p -> p.getTenantId().equals(AuthContextHolder.getTenantId()))
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setBarcode(dto.getBarcode());
        product.setUnit(dto.getUnit());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setMinimumStock(dto.getMinimumStock());
        product.setActive(dto.isActive());

        if (!dto.getCategoryId().equals(product.getCategory().getId())) {
            Category category = categoryRepo.findByCategoryIdAndTenantId(dto.getCategoryId(), AuthContextHolder.getTenantId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);

        }
        // Recalculate status when updating stock
        product.setStatus(calculateProductStatus(product));

        return productRepo.save(product);
    }

    @Override
    public void delete(Long id) {
        productRepo.deleteById(id);
    }

    @Override
    public Long getTotalProductsCount() {
        return productRepo.countByTenantId(AuthContextHolder.getTenantId());
    }

    @Override
    public Long getActiveProductsCount() {
        return productRepo.countActiveProducts(AuthContextHolder.getTenantId());
    }

    @Override
    public Long getLowStockProductsCount() {
        return productRepo.countLowStockProducts(AuthContextHolder.getTenantId());
    }

    @Override
    public Long getOutOfStockProductsCount() {
        return productRepo.countOutOfStockProducts(AuthContextHolder.getTenantId());
    }

    @Override
    public List<Product> getLowStockProducts() {
        return productRepo.findByTenantIdAndStockLessThan(
                AuthContextHolder.getTenantId(),
                productRepo.getMinimumStockThreshold(AuthContextHolder.getTenantId())
        );
    }

    @Override
    public List<CategoryProductCountResponse> getProductCountByCategory() {
        List<Map<String, Object>> results = productRepo.countProductsByCategory(AuthContextHolder.getTenantId());
        return results.stream()
                .map(result -> CategoryProductCountResponse.builder()
                        .categoryName((String) result.get("categoryName"))
                        .productCount((Long) result.get("productCount"))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Long getProductsWithoutMinimumStockCount() {
        return productRepo.countProductsWithoutMinimumStock(AuthContextHolder.getTenantId());
    }

    private ProductResponse mapToResponse(Product product) {
        Category category = product.getCategory();
        return ProductResponse.builder().sku(product.getSku()).barcode(product.getBarcode()).price(product.getPrice()).stock(product.getStock())
                .minimumStock(product.getMinimumStock()).unit(product.getUnit()).name(product.getName())
                .categoryId(product.getCategory().getId()).id(product.getId()).createdAt(product.getCreatedAt())
                .categoryName(product.getCategory().getName())
                .category(categoryMapper.createSafeCategoryResponse(category))
                .status(product.getStatus() != null ? product.getStatus().name() : "UNKNOWN") // Handle null
                .isActive(product.isActive())
                .updatedAt(product.getUpdatedAt()).createdBy(product.getCreatedBy()).updatedBy(product.getUpdatedBy())
                .build();

    }



    private ProductStatus calculateProductStatus(Product product) {
        Integer stock = product.getStock();
        Integer minimumStock = product.getMinimumStock();
        if (stock == null) stock = 0;
        if (minimumStock == null) minimumStock = 0;

        if (stock > 0 && stock > minimumStock) {
            try {
                if (!product.getCategory().isActive()) {
                    return ProductStatus.DISABLED;
                }
            } catch (LazyInitializationException e) {
//                return ProductStatus.UNKNOWN;
            }
        }
        if (stock == 0) {
            return ProductStatus.OUT_OF_STOCK;
        } else if (stock <= minimumStock) {
            return ProductStatus.LOW_STOCK;
        } else {
            return ProductStatus.ACTIVE;
        }
    }
}