package com.tanzeem.iam.controller;
import com.tanzeem.iam.dto.*;
import com.tanzeem.iam.entity.RefreshToken;
import com.tanzeem.iam.repository.RefreshTokenRepository;
import com.tanzeem.iam.service.AuthService;
import com.tanzeem.iam.service.RefreshTokenService;
import com.tanzeem.security.common.JwtUtil;
import com.tanzeem.security.exception.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String token = request.get("refreshToken");

        return refreshTokenRepository.findByToken(token)
                .map(rt -> {
                    if (refreshTokenService.isExpired(rt)) {
                        refreshTokenRepository.delete(rt);
                        throw new TokenExpiredException("Refresh token expired");
                    }

                    String newAccessToken = jwtUtil.generateToken(rt.getUser().getEmail(), rt.getUser().getRoles().stream()
                            .map(role -> role.getName())
                            .toList(), rt.getUser().getTenantId());
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(rt.getUser());
                    return ResponseEntity.ok(AuthResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(newRefreshToken.getToken())
                            .build());
                })
                .orElseGet(() -> {
                    throw new TokenExpiredException("Invalid refresh token");
                });
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String token = request.get("refreshToken");

        return refreshTokenRepository.findByToken(token)
                .map(rt -> {
                    refreshTokenRepository.delete(rt);
                    return ResponseEntity.ok("Logged out successfully");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token"));
    }



}
