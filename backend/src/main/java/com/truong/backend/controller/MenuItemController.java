package com.truong.backend.controller;

import com.truong.backend.dto.request.MenuItemRequestDTO;
import com.truong.backend.dto.response.ApiResponse;
import com.truong.backend.dto.response.MenuItemResponseDTO;
import com.truong.backend.entity.MenuItem;
import com.truong.backend.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    // Lấy danh sách món (Tất cả vai trò)
    @GetMapping()
    public ResponseEntity<ApiResponse<List<MenuItem>>> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemService.getAllMenuItems();
        return ResponseEntity.ok(
                new ApiResponse<>("success", "Get all menu items", menuItems)
        );
    }

    // Lấy món theo ID (Tất cả vai trò)
    @GetMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<MenuItem>> getMenuItemById(@PathVariable Long id) {
        MenuItem menuItem = menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(
                new ApiResponse<>("success", "Get menu item by id", menuItem)
        );
    }

    // Tạo món mới (Admin)
    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<MenuItem>> createMenuItem(
            @RequestPart("itemName") @Valid String itemName,
            @RequestPart("description") @Valid String description,
            @RequestPart("price") @Valid String price,
            @RequestPart(value = "image", required = false) MultipartFile image)
    {

        MenuItemRequestDTO request = new MenuItemRequestDTO(
                itemName,description,Double.parseDouble(price)
        );
        MenuItem menuItem = menuItemService.createMenuItem(request, image);
        return ResponseEntity.ok(
                new ApiResponse<>("success", "Create menu item", menuItem)
        );
    }

    // Cập nhật món (Admin)
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<MenuItem>> updateMenuItem(
            @PathVariable Long id,
            @RequestPart("itemName") @Valid String itemName,
            @RequestPart("description") @Valid String description,
            @RequestPart("price") @Valid String price,
            @RequestPart(value = "image", required = false) MultipartFile image)
    {
        MenuItemRequestDTO request = new MenuItemRequestDTO(
                itemName,description,Double.parseDouble(price)
        );

        MenuItem menuItem = menuItemService.updateMenuItem(id, request, image);
        return ResponseEntity.ok(
                new ApiResponse<>("success", "Update menu item", menuItem)
        );
    }

    // Xóa món (Admin)
    @DeleteMapping( "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.ok(
                new ApiResponse<>( "success", "Delete menu item",null)
        );
    }
}