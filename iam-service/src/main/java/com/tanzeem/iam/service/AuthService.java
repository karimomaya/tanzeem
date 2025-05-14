package com.tanzeem.iam.service;
import com.tanzeem.iam.dto.*;
import com.tanzeem.iam.entity.*;
import com.tanzeem.iam.repository.*;
import com.tanzeem.security.common.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

            String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRoles().stream()
                    .map(Role::getName)
                    .toList(), user.getTenantId());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);


            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        Role defaultRole = roleRepository.findByNameAndTenantId("ROLE_USER", request.getTenantId())
                .orElseGet(() -> {
                    // Create the default role for the tenant if it doesn't exist
                    Role newRole = Role.builder()
                            .name("ROLE_USER")
                            .tenantId(request.getTenantId())
                            .build();
                    return roleRepository.save(newRole);
                });
        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .roles(Set.of(defaultRole))
                .tenantId(request.getTenantId())
                .enabled(true)
                .build();

        userRepository.save(newUser);
        return login(new LoginRequest(request.getEmail(), request.getPassword(), request.getTenantId()));
    }
}
