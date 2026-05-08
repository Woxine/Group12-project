package com.group12.backend.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private static final long EXPIRATION_TIME = 86400000; // 24小时

    private final Key secretKey;

    public JwtUtil(@Value("${jwt.secret:}") String configuredSecret) {
        byte[] keyBytes;
        if (configuredSecret != null && !configuredSecret.isBlank()) {
            keyBytes = configuredSecret.getBytes(StandardCharsets.UTF_8);
        } else {
            // No secret configured — generate a random one (tokens won't survive restart)
            String random = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
            keyBytes = random.getBytes(StandardCharsets.UTF_8);
            log.warn("JWT_SECRET not set. Generated a random key — tokens will be invalidated on restart. Set JWT_SECRET env var for production.");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Token
     */
    public String generateToken(String email, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证 Token 有效性
     */
    public boolean validateToken(String token, String givenEmail) {
        final String email = extractEmail(token);
        return (email.equals(givenEmail) && !isTokenExpired(token));
    }

    /**
     * 简单验证 Token 是否过期或被篡改 (Interceptor 使用)
     */
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
