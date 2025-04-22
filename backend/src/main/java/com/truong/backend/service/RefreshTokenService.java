package com.truong.backend.service;

import com.truong.backend.entity.RefreshToken;
import com.truong.backend.entity.User;
import com.truong.backend.repository.RefreshTokenRepository;
import com.truong.backend.repository.UserRepository;
import com.truong.backend.config.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public String createRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Xóa refresh token cũ của user (nếu có)
        refreshTokenRepository.deleteByUser(user);

        // Tạo refresh token mới (dạng JWT)
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        String token = jwtUtil.generateRefreshToken(email);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(jwtUtil.generateRefreshTokenExpiryDate());
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token has expired");
        }
        return token;
    }

    public void validateRefreshToken(String token, String email) {
        if (!jwtUtil.extractTokenType(token).equals("refresh")) {
            throw new RuntimeException("Invalid token type: must be refresh token");
        }
        if (!jwtUtil.validateToken(token, email)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }
}