package com.tanzeem.iam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
    private String fullName;
    @NotBlank(message = "Tenant ID is required")
    private String tenantId;
}
