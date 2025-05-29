package com.tanzeem.product.service;

import com.tanzeem.product.dto.ProductDto;
import com.tanzeem.product.dto.ProductResponse;
import com.tanzeem.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductDto dto);
    Page<ProductResponse> getAll(String search, Pageable pageable);
    Product update(Long id, ProductDto dto);
    void delete(Long id);
    List<Product> getLowStockProducts();
}
