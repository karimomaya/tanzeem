package com.tanzeem.product.service.impl;

import com.tanzeem.product.dto.CategoryRequest;
import com.tanzeem.product.dto.CategoryResponse;
import com.tanzeem.product.entity.Category;
import com.tanzeem.product.mapper.CategoryMapper;
import com.tanzeem.product.repository.CategoryRepository;
import com.tanzeem.product.service.CategoryService;
import com.tanzeem.security.common.AuthContextHolder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setTenantId(AuthContextHolder.getTenantId());
        category.setIcon(request.getIcon());
        category.setDescription(request.getDescription());
        category.setActive(request.isActive());
        category.setColor(request.getColor());

        categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    public Page<CategoryResponse> getAllCategories(String search, boolean isActive, Pageable pageable) {
        search = search == null ? "" : search.trim();
        Page<Object[]> categories = categoryRepository.findByTenantIdAndIsActiveAndNameContainingIgnoreCase(
                    AuthContextHolder.getTenantId(), search, isActive, pageable);
        return categories.map(result -> {
            Category category = (Category) result[0];
            int activity = ((Number) result[1]).intValue();
            long productCount = ((Number) result[2]).longValue();
            return categoryMapper.mapToResponse(category).toBuilder()
                    .activity(activity)
                    .productCount(productCount)
                    .build();
        });
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setDescription(request.getDescription());
        category.setActive(request.isActive());
        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private CategoryResponse mapToResponse(Category category) {
        Long productCount = categoryRepository.countProductsByCategoryId(category.getId());
        return categoryMapper.mapToResponse(category);
    }


}
