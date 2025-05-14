package com.tanzeem.product.service;

import com.tanzeem.product.dto.CategoryRequest;
import com.tanzeem.product.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    List<CategoryResponse> getAllCategories();
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
}
