package com.tanzeem.iam.service;

import com.tanzeem.iam.entity.RefreshToken;
import com.tanzeem.iam.entity.User;
import com.tanzeem.iam.repository.RefreshTokenRepository;
import com.tanzeem.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepo;
    private final UserRepository userRepo;

    private final long refreshTokenDurationMs = 1000 * 60 * 60 * 24 * 7; // 7 days

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(new Date(System.currentTimeMillis() + refreshTokenDurationMs))
                .build();

        refreshTokenRepo.deleteByUser(user); // Only one token per user
        return refreshTokenRepo.save(token);
    }

    public boolean isExpired(RefreshToken token) {
        return token.getExpiryDate().before(new Date());
    }
}
