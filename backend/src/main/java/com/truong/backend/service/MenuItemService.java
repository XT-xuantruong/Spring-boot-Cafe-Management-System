package com.truong.backend.service;

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
    public MenuItem createMenuItem(String itemName, Double price, String category, MultipartFile image) throws IOException {
        String imageUrl = image != null && !image.isEmpty() ? cloudinaryService.uploadImage(image) : null;

        MenuItem menuItem = new MenuItem();
        menuItem.setItemName(itemName);
        menuItem.setPrice(price);
        menuItem.setCategory(category);
        menuItem.setImageUrl(imageUrl);
        menuItem.setIsAvailable(true);
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
    public MenuItem updateMenuItem(Long id, String itemName, Double price, String category, MultipartFile image) throws IOException {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with ID: " + id));
        menuItem.setItemName(itemName);
        menuItem.setPrice(price);
        menuItem.setCategory(category);
        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(image);
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

    // Lấy món theo danh mục (Tất cả vai trò)
    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategory(category);
    }
}
