package com.tanzeem.product.service;

import com.tanzeem.product.dto.ProductDto;
import com.tanzeem.product.entity.Product;

import java.util.List;

public interface ProductService {
    Product create(ProductDto dto);
    List<Product> getAll();
    Product update(Long id, ProductDto dto);
    void delete(Long id);
    List<Product> getLowStockProducts();
}
