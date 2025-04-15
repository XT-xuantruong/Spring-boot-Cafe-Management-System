package com.truong.backend.repository;

import com.truong.backend.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    // Lấy danh sách món ăn theo danh mục
    List<MenuItem> findByCategory(String category);

    // Lấy danh sách món ăn đang có sẵn
    List<MenuItem> findByIsAvailableTrue();
}
