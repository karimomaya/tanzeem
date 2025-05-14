package com.tanzeem.product.service.impl;

import com.tanzeem.product.dto.CategoryRequest;
import com.tanzeem.product.dto.CategoryResponse;
import com.tanzeem.product.entity.Category;
import com.tanzeem.product.repository.CategoryRepository;
import com.tanzeem.product.service.CategoryService;
import com.tanzeem.tenantlib.context.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setTenantId(TenantContext.getTenantId());

        categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByTenantId(TenantContext.getTenantId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        category.setName(request.getName());
        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
