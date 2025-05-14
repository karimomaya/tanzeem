package com.tanzeem.iam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanzeem.iam.dto.LoginRequest;
import com.tanzeem.iam.dto.RegisterRequest;
import com.tanzeem.iam.entity.RefreshToken;
import com.tanzeem.iam.entity.User;
import com.tanzeem.iam.repository.RefreshTokenRepository;
import com.tanzeem.iam.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    final String TENANT_ID = "tanzeem";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    public void testRegisterUserSuccess() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("test1@example.com")
                .password("test123")
                .fullName("Test User")
                .tenantId("Shopify")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("admin@erp.com", "admin123", TENANT_ID);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    public void testLoginFailure() throws Exception {
        LoginRequest request = new LoginRequest("admin@erp.com", "wrongpass", TENANT_ID);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void testExpiredRefreshToken() throws Exception {
        // Create expired token manually (simulate in DB)
        User user = userRepository.findByEmail("admin@erp.com").orElseThrow();
        refreshTokenRepository.deleteByUser(user);

        RefreshToken expiredToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(new Date(System.currentTimeMillis() - 1000)) // expired
                .build();
        refreshTokenRepository.save(expiredToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + expiredToken.getToken() + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Refresh token expired")));
    }

    /*@Test
    public void testInvalidRefreshToken() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"invalid-token-value\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid refresh token")));
    }


    @Test
    public void testValidRefreshToken() throws Exception {
        // Login to get a valid refresh token
        LoginRequest request = new LoginRequest("admin@erp.com", "admin123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = JsonPath.read(result.getResponse().getContentAsString(), "$.refreshToken");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }*/
}
