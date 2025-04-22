package com.truong.backend.service;

import com.truong.backend.dto.MenuItemRequest;
import com.truong.backend.entity.MenuItem;
import com.truong.backend.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // Tạo món mới (Admin)
    public MenuItem createMenuItem(MenuItemRequest menuItemRequest, MultipartFile image) {
        if (menuItemRepository.existsByItemName(menuItemRequest.getItemName())) {
            throw new IllegalArgumentException("Món ăn với tên '" + menuItemRequest.getItemName() + "' đã tồn tại");
        }
        MenuItem menuItem = new MenuItem();
        menuItem.setItemName(menuItemRequest.getItemName());
        menuItem.setDescription(menuItemRequest.getDescription());
        menuItem.setPrice(menuItemRequest.getPrice());

        // Xử lý upload ảnh nếu có
        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(image,"menu");
            menuItem.setImageUrl(imageUrl);
        }

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
    public MenuItem updateMenuItem(Long id, MenuItemRequest menuItemRequest, MultipartFile image) {
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

        // Xử lý upload ảnh nếu có
        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(image, "menu");
            menuItem.setImageUrl(imageUrl);
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

}