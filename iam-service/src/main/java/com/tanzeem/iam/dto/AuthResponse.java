package com.tanzeem.iam.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}
