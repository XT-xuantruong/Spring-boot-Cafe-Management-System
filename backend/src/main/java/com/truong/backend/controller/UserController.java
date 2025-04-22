package com.truong.backend.controller;

import com.truong.backend.dto.request.UserRequestDTO;
import com.truong.backend.dto.response.ApiResponse;
import com.truong.backend.dto.response.UserResponseDTO;
import com.truong.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getProfile() {
        UserResponseDTO userResponse = userService.getCurrentUserProfile();
        return ResponseEntity.ok(new ApiResponse<>(
                "success", "Profile retrieved successfully", userResponse));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateProfile(
            @Valid @RequestBody UserRequestDTO request
    ) {
        UserResponseDTO updatedUser = userService.updateUserProfile(request);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "success",
                        "Profile updated successfully",
                        updatedUser)
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "success",
                        "Users retrieved successfully",
                        users)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(
            @PathVariable Long id
    ) {
        UserResponseDTO userResponse = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "User retrieved successfully",
                userResponse)
        );
    }
    @PostMapping("")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(
            @Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO newUser = userService.createUser(request);
        return ResponseEntity.ok(new ApiResponse<>("success", "User registered successfully", newUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDTO request
    ) {
        UserResponseDTO updatedUser = userService.userUpdate(id,request);
        return ResponseEntity.ok(new ApiResponse<>("success", "User updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "User deleted successfully", null));
    }
}