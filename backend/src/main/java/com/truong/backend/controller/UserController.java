package com.truong.backend.controller;

import com.truong.backend.dto.ApiResponse;
import com.truong.backend.dto.UpdateProfileRequest;
import com.truong.backend.dto.UpdateUserRequest;
import com.truong.backend.dto.UserResponse;
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
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        UserResponse userResponse = userService.getCurrentUserProfile();
        return ResponseEntity.ok(new ApiResponse<>("success", "Profile retrieved successfully", userResponse));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserResponse updatedUser = userService.updateUserProfile(request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Profile updated successfully", updatedUser));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>("success", "Users retrieved successfully", users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "User retrieved successfully", userResponse));
    }

    @PutMapping()
    public ResponseEntity<ApiResponse<UserResponse>> updateUser( @RequestBody UpdateUserRequest request) {
        UserResponse updatedUser = userService.userUpdate(request);
        return ResponseEntity.ok(new ApiResponse<>("success", "User updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "User deleted successfully", null));
    }
}