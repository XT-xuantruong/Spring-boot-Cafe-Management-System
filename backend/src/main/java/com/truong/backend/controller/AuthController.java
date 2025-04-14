package com.truong.backend.controller;

import com.truong.backend.config.JwtUtil;
import com.truong.backend.dto.*;
import com.truong.backend.entity.RefreshToken;
import com.truong.backend.entity.Role;
import com.truong.backend.entity.User;
import com.truong.backend.repository.UserRepository;
import com.truong.backend.service.CustomUserDetailsService;
import com.truong.backend.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService,
                          UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        AuthResponse tokens = new AuthResponse(accessToken,refreshToken);
        return ResponseEntity.ok(new ApiResponse<>("success", "Login successful", tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String email = user.getEmail();
                    refreshTokenService.validateRefreshToken(refreshToken, email);
                    String newAccessToken = jwtUtil.generateAccessToken(email);
                    String newRefreshToken = refreshTokenService.createRefreshToken(email);
                    AuthResponse tokens = new AuthResponse(newAccessToken,newRefreshToken);
                    return ResponseEntity.ok(new ApiResponse<>("success", "Token refreshed successfully", tokens));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("error", "Email already exists", null));
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());
        user.setPhone(registerRequest.getPhone());
        user.setAddress(registerRequest.getAddress());
        user.setRole(Role.USER);
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse<>("success", "User registered successfully", null));
    }
}