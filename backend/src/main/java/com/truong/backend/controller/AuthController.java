package com.truong.backend.controller;

import com.truong.backend.config.JwtUtil;
import com.truong.backend.dto.request.AuthRequest;
import com.truong.backend.dto.request.UserRequestDTO;
import com.truong.backend.dto.response.ApiResponse;
import com.truong.backend.dto.response.AuthResponse;
import com.truong.backend.entity.RefreshToken;
import com.truong.backend.entity.enums.Role;
import com.truong.backend.entity.User;
import com.truong.backend.repository.UserRepository;
import com.truong.backend.service.CustomUserDetailsService;
import com.truong.backend.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            CustomUserDetailsService userDetailsService,
            RefreshTokenService refreshTokenService,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody AuthRequest authRequest
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(
                authRequest.getEmail()
        );
        String accessToken = jwtUtil.generateAccessToken(
                userDetails.getUsername()
        );
        String refreshToken = refreshTokenService.createRefreshToken(
                userDetails.getUsername()
        );

        AuthResponse tokens = new AuthResponse(
                accessToken,
                refreshToken
        );
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "success",
                        "Login successful",
                        tokens
                )
        );
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody UserRequestDTO request
    ) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    "error",
                    "Email already exists",
                    null
            ));
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "success",
                        "User registered successfully",
                        null
                )
        );
    }
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestBody String request
    ) {
        return refreshTokenService.findByToken(request)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
                String email = user.getEmail();
                refreshTokenService.validateRefreshToken(request, email);
                String newAccessToken = jwtUtil.generateAccessToken(email);
                String newRefreshToken = refreshTokenService.createRefreshToken(email);
                AuthResponse tokens = new AuthResponse(newAccessToken,newRefreshToken);
                return ResponseEntity.ok(new ApiResponse<>(
                        "success",
                        "Token refreshed successfully",
                        tokens
                ));
            })
            .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }



    @PostMapping(value = "/logout")
    public ResponseEntity<ApiResponse<Object>> logout(
            @RequestBody String request
    ) {
        return refreshTokenService.findByToken(request)
                .map(token -> {
                    refreshTokenService.deleteRefreshToken(request);
                    return ResponseEntity.ok(
                            new ApiResponse<>(
                                    "success",
                                    "Logged out successfully",
                                    null
                            ));
                })
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(new ApiResponse<>(
                                "error",
                                "Refresh token not found",
                                null
                        ))
                );
    }
}