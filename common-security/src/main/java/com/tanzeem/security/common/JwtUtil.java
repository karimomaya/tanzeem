package com.tanzeem.security.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    // Ideally, move to application.yml and inject via @Value
    private static final String SECRET_KEY = "your-very-secret-key-which-should-be-256-bits-long!!!";
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Generate JWT Token
    public String generateToken(String email, List<String> roles, String tenantId) {
        return this.generateToken(email, roles, tenantId, EXPIRATION_TIME);
    }

    public String generateToken(String email, List<String> roles, String tenantId,  Long expirationTime) {
        if (expirationTime == null) expirationTime = EXPIRATION_TIME;

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .claim("tenantId", tenantId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token (signature and expiry)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Extract username (subject)
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // Extract roles
    public List<String> getRoles(String token) {
        Object roles = getClaims(token).get("roles");
        if (roles instanceof List<?>) {
            return ((List<?>) roles).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    // Extract tenant ID
    public String getTenantId(String token) {
        return (String) getClaims(token).get("tenantId");
    }

    // Common claim extractor
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
