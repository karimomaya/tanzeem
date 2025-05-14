package com.tanzeem.iam;

import com.jayway.jsonpath.JsonPath;
import com.tanzeem.iam.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAccessMeWithToken() throws Exception {
        final String TENANT_ID = "tanzeem";
        // First: login to get token
        LoginRequest request = new LoginRequest("admin@erp.com", "admin123", TENANT_ID);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@erp.com"));
    }

    @Test
    public void testAccessMeWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden()); // or UNAUTHORIZED depending on config
    }
}
