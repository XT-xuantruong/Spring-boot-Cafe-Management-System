package com.truong.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {
    private static final Log logger = LogFactory.getLog(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        try {
            return Keys.hmacShaKeyFor(secret.getBytes());
        } catch (Exception e) {
            logger.error("Failed to create signing key: " + e.getMessage(), e);
            throw new IllegalStateException("Invalid JWT secret", e);
        }
    }

    public String generateAccessToken(String email) {
        logger.debug("Generating access token for: " + email);
        return Jwts.builder()
                .subject(email)
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        logger.debug("Generating refresh token for: " + email);
        return Jwts.builder()
                .subject(email)
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Instant generateRefreshTokenExpiryDate() {
        return Instant.now().plusMillis(refreshTokenExpiration);
    }

    public String extractUsername(String token) {
        try {
            Claims claims = getClaims(token);
            String username = claims.getSubject();
            logger.debug("Extracted username: " + username);
            return username;
        } catch (JwtException e) {
            logger.error("Failed to extract username from token: " + e.getMessage());
            return null;
        }
    }

    public String extractTokenType(String token) {
        try {
            Claims claims = getClaims(token);
            String type = claims.get("type", String.class);
            logger.debug("Extracted token type: " + type);
            return type;
        } catch (JwtException e) {
            logger.error("Failed to extract token type: " + e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token, String email) {
        try {
            String tokenEmail = extractUsername(token);
            if (tokenEmail == null) {
                logger.warn("No username in token");
                return false;
            }
            boolean isValid = tokenEmail.equals(email) && !isTokenExpired(token);
            logger.debug("Token validation for " + email + ": " + (isValid ? "valid" : "invalid"));
            return isValid;
        } catch (Exception e) {
            logger.error("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            logger.error("Failed to parse JWT claims: " + e.getMessage());
            throw e;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaims(token).getExpiration();
            boolean expired = expiration.before(new Date());
            logger.debug("Token expiration check: " + (expired ? "expired" : "valid"));
            return expired;
        } catch (JwtException e) {
            logger.error("Failed to check token expiration: " + e.getMessage());
            return true; // Treat invalid tokens as expired
        }
    }
}