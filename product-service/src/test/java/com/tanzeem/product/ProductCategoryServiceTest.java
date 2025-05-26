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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
        cat1.setId(1L);
        cat1.setName("A");
        cat1.setTenantId("tenant123");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("B");
        cat2.setTenantId("tenant123");

        Page<Category> page = new PageImpl<>(List.of(cat1, cat2));

        when(repository.findByTenantId(eq("tenant123"), any(Pageable.class))).thenReturn(page);

        Page<CategoryResponse> responses = service.getAllCategories("",Pageable.unpaged());

        assertEquals(2, responses.getContent().size());
        assertEquals("A", responses.getContent().get(0).getName());
        assertEquals("B", responses.getContent().get(1).getName());
        verify(repository, times(1)).findByTenantId(eq("tenant123"), any(Pageable.class));
    }
}
