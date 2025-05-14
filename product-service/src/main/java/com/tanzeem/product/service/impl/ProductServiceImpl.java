package com.tanzeem.product.service.impl;

import com.tanzeem.product.dto.ProductDto;
import com.tanzeem.product.entity.Category;
import com.tanzeem.product.entity.Product;
import com.tanzeem.product.repository.CategoryRepository;
import com.tanzeem.product.repository.ProductRepository;
import com.tanzeem.product.service.ProductService;
import com.tanzeem.tenantlib.context.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;


    @Override
    public Product create(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setBarcode(dto.getBarcode());
        product.setUnit(dto.getUnit());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setMinimumStock(dto.getMinimumStock());
        product.setTenantId(TenantContext.getTenantId());

        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);

        return productRepo.save(product);
    }

    @Override
    public List<Product> getAll() {
        return productRepo.findByTenantId(TenantContext.getTenantId());
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
                TenantContext.getTenantId(),
                productRepo.getMinimumStockThreshold(TenantContext.getTenantId())
        );
    }
}