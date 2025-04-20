package com.truong.backend.service;

import com.truong.backend.dto.MenuItemRequest;
import com.truong.backend.entity.MenuItem;
import com.truong.backend.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // Tạo món mới (Admin)
    public MenuItem createMenuItem(MenuItemRequest menuItemRequest) {
        if (menuItemRepository.existsByItemName(menuItemRequest.getItemName())) {
            throw new IllegalArgumentException("Món ăn với tên '" + menuItemRequest.getItemName() + "' đã tồn tại");
        }
        MenuItem menuItem = MenuItem.builder()
                .itemName(menuItemRequest.getItemName())
                .description(menuItemRequest.getDescription())
                .price(menuItemRequest.getPrice())
                .category(menuItemRequest.getCategory())
                .imageUrl(menuItemRequest.getImageUrl()) // Sử dụng imageUrl từ request
                .isAvailable(menuItemRequest.getIsAvailable() != null ? menuItemRequest.getIsAvailable() : true)
                .build();
        return menuItemRepository.save(menuItem);
    }

    // Lấy danh sách món (Tất cả vai trò)
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    // Lấy món theo ID (Tất cả vai trò)
    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with ID: " + id));
    }

    // Cập nhật món (Admin)
    public MenuItem updateMenuItem(Long id, MenuItemRequest menuItemRequest) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with ID: " + id));

        if (menuItemRequest.getItemName() != null) {
            menuItem.setItemName(menuItemRequest.getItemName());
        }
        if (menuItemRequest.getDescription() != null) {
            menuItem.setDescription(menuItemRequest.getDescription());
        }
        if (menuItemRequest.getPrice() != null) {
            menuItem.setPrice(menuItemRequest.getPrice());
        }
        if (menuItemRequest.getCategory() != null) {
            menuItem.setCategory(menuItemRequest.getCategory());
        }
        if (menuItemRequest.getIsAvailable() != null) {
            menuItem.setIsAvailable(menuItemRequest.getIsAvailable());
        }
        if (menuItemRequest.getImageUrl() != null) {
            menuItem.setImageUrl(menuItemRequest.getImageUrl()); // Cập nhật imageUrl
        }

        return menuItemRepository.save(menuItem);
    }

    // Xóa món (Admin)
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new IllegalArgumentException("Menu item not found with ID: " + id);
        }
        menuItemRepository.deleteById(id);
    }

    // Lấy món theo danh mục (Tất cả vai trò)
    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategory(category);
    }
}