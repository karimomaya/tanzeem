package com.tanzeem.product;

import com.tanzeem.product.controller.CategoryController;
import com.tanzeem.product.dto.CategoryRequest;
import com.tanzeem.product.dto.CategoryResponse;
import com.tanzeem.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ImportAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private com.tanzeem.security.common.JwtAuthFilter jwtAuthFilter;

    @MockBean
    private CategoryService categoryService;

    @Test
    void testCreateCategory() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Electronics");
        request.setIcon("mdi-laptop");
        request.setDescription("Category for electronic items");
        request.setActive(true);

        CategoryResponse response = CategoryResponse.builder().id(1L).name("Electronics").icon("mdi-laptop").description("Description 1").isActive(true).color("#000").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).createdBy("test").updatedBy("test").productCount(0L).build();


        when(categoryService.createCategory(any())).thenReturn(response);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "name": "Electronics",
                      "icon": "mdi-laptop",
                      "description": "Category for electronic items",
                      "active": true
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.icon").value("mdi-laptop"))
                .andExpect(jsonPath("$.description").value("Category for electronic items"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testGetAllCategories() throws Exception {
        CategoryResponse response1 = CategoryResponse.builder().id(1L).name("Electronics").icon("mdi-laptop").description("Description 1").isActive(true).color("#000").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).createdBy("test").updatedBy("test").productCount(0L).build();

        CategoryResponse response2 = CategoryResponse.builder().id(2L).name("Groceries").icon("mdi-cart").description("Description 2").isActive(true).color("#fff").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).createdBy("test").updatedBy("test").productCount(0L).build();

        Page<CategoryResponse> page = new PageImpl<>(List.of(response1, response2));

        when(categoryService.getAllCategories("", true,any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/categories")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Electronics"))
                .andExpect(jsonPath("$.content[1].name").value("Groceries"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testUpdateCategory() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Updated");
        request.setIcon("mdi-update");
        request.setDescription("Updated description");
        request.setActive(true);

        CategoryResponse response = CategoryResponse.builder().id(1L).name("Updated").icon("mdi-update").description("Updated description").isActive(true).color("#000").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).createdBy("test").updatedBy("test").productCount(0L).build();

        when(categoryService.updateCategory(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Updated",
                          "icon": "mdi-update",
                          "description": "Updated description",
                          "active": true
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.icon").value("mdi-update"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testDeleteCategory() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }
}
