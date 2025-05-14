package com.tanzeem.iam.controller;
import com.tanzeem.iam.entity.User;
import com.tanzeem.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/me")
    public User getAuthenticatedUser(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
        return user.orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/admin/test")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminOnlyEndpoint() {
        return "Hello Admin!";
    }
}
