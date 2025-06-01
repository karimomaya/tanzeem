package com.tanzeem.product.service.impl;

import com.tanzeem.product.dto.CategoryRequest;
import com.tanzeem.product.dto.CategoryResponse;
import com.tanzeem.product.dto.CategoryStatsResponse;
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
import java.util.Map;
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
    public Page<CategoryResponse> getAllCategories(String search, String isActive, Pageable pageable) {
        search = search == null ? "" : search.trim();
        Boolean isActiveParam = "all".equalsIgnoreCase(isActive) ? null : Boolean.valueOf(isActive);
        Page<Object[]> categories = categoryRepository.findByTenantIdAndIsActiveAndNameContainingIgnoreCase(
                    AuthContextHolder.getTenantId(), search, isActiveParam, pageable);
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

    @Override
    public CategoryStatsResponse getCategoryStats() {
        String tenantId = AuthContextHolder.getTenantId();

        Long total = categoryRepository.countByTenantId(tenantId);
        Long active = categoryRepository.countActiveCategories(tenantId);
        Long inactive = categoryRepository.countInactiveCategories(tenantId);
        Long categoriesWithoutProducts = categoryRepository.countCategoriesWithoutProducts(tenantId);
        Double averageProducts = categoryRepository.getAverageProductsPerCategory(tenantId);

        // Get top category
        List<Object[]> categoriesWithCount = categoryRepository.getCategoriesWithProductCount(tenantId);
        String topCategoryName = null;
        Long topCategoryProductCount = 0L;

        if (!categoriesWithCount.isEmpty()) {
            Object[] topCategory = categoriesWithCount.get(0);
            topCategoryName = (String) topCategory[0];
            topCategoryProductCount = ((Number) topCategory[1]).longValue();
        }

        // Calculate active percentage
        Double activePercentage = total > 0 ? (active.doubleValue() / total.doubleValue()) * 100 : 0.0;

        return CategoryStatsResponse.builder()
                .total(total)
                .active(active)
                .inactive(inactive)
                .averageProducts(averageProducts != null ? Math.round(averageProducts * 100.0) / 100.0 : 0.0)
                .topCategoryName(topCategoryName)
                .topCategoryProductCount(topCategoryProductCount)
                .categoriesWithoutProducts(categoriesWithoutProducts)
                .activePercentage(Math.round(activePercentage * 100.0) / 100.0)
                .build();
    }

    private CategoryResponse mapToResponse(Category category) {
        return categoryMapper.mapToResponse(category);
    }


}
