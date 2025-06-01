package com.tanzeem.product.service.impl;

import com.tanzeem.product.dto.CategoryResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        product.setImageUrl(dto.getImageUrl());
        product.setTenantId(AuthContextHolder.getTenantId());
        product.setStatus(calculateProductStatus(dto.getStock(), dto.getMinimumStock()));

        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);

        product=  productRepo.save(product);
        return mapToResponse(product);
    }
    @Transactional
    @Override
    public Page<ProductResponse> getAll(String search, Pageable pageable) {
        if (search == null) search = "";
        Page<Product> products = productRepo.findByTenantIdAndNameContainingIgnoreCase(
                    AuthContextHolder.getTenantId(), search, pageable);
        return products.map(this::mapToResponse);
    }

    @Override
    public Product update(Long id, ProductDto dto) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setBarcode(dto.getBarcode());
        product.setUnit(dto.getUnit());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setMinimumStock(dto.getMinimumStock());
        product.setImageUrl(dto.getImageUrl());
        // Recalculate status when updating stock
        product.setStatus(calculateProductStatus(dto.getStock(), dto.getMinimumStock()));

        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);

        return productRepo.save(product);
    }

    @Override
    public void delete(Long id) {
        productRepo.deleteById(id);
    }

    @Override
    public List<Product> getLowStockProducts() {
        return productRepo.findByTenantIdAndStockLessThan(
                AuthContextHolder.getTenantId(),
                productRepo.getMinimumStockThreshold(AuthContextHolder.getTenantId())
        );
    }

    private ProductResponse mapToResponse(Product product) {
        Category category = product.getCategory();
        return ProductResponse.builder().sku(product.getSku()).barcode(product.getBarcode()).price(product.getPrice()).stock(product.getStock())
                .minimumStock(product.getMinimumStock()).unit(product.getUnit()).name(product.getName())
                .categoryId(product.getCategory().getId()).id(product.getId()).createdAt(product.getCreatedAt())
                .categoryName(product.getCategory().getName())
                .category(categoryMapper.mapToResponse(category))
                .status(product.getStatus().name())
                .updatedAt(product.getUpdatedAt()).createdBy(product.getCreatedBy()).updatedBy(product.getUpdatedBy())
                .imageUrl(product.getImageUrl())
                .build();

    }

    private ProductStatus calculateProductStatus(Integer stock, Integer minimumStock) {
        if (stock == null) stock = 0;
        if (minimumStock == null) minimumStock = 0;

        if (stock == 0) {
            return ProductStatus.OUT_OF_STOCK;
        } else if (stock <= minimumStock) {
            return ProductStatus.LOW_STOCK;
        } else {
            return ProductStatus.ACTIVE;
        }
    }
}