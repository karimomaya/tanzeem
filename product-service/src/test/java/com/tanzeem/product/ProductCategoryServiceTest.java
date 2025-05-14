package com.tanzeem.product;

import com.tanzeem.product.dto.CategoryRequest;
import com.tanzeem.product.dto.CategoryResponse;
import com.tanzeem.product.entity.Category;
import com.tanzeem.product.repository.CategoryRepository;
import com.tanzeem.product.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductCategoryServiceTest {
    @InjectMocks
    private CategoryServiceImpl service;

    @Mock
    private CategoryRepository repository;

    @Test
    void createCategory_shouldSaveAndReturnResponse() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Electronics");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Electronics");
        savedCategory.setTenantId("tenant123");

        when(repository.save(any())).thenReturn(savedCategory);

        CategoryResponse response = service.createCategory(request);

        assertEquals("Electronics", response.getName());
        verify(repository, times(1)).save(any());
    }

    @Test
    void getAllCategories_shouldReturnAllForTenant() {
        Category cat1 = new Category();
        cat1.setId(1L); cat1.setName("A"); cat1.setTenantId("tenant123");

        Category cat2 = new Category();
        cat2.setId(2L); cat2.setName("B"); cat2.setTenantId("tenant123");

        when(repository.findByTenantId(any())).thenReturn(List.of(cat1, cat2));

        List<CategoryResponse> responses = service.getAllCategories();

        assertEquals(2, responses.size());
    }
}
