package com.truong.backend.repository;

import com.truong.backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Lấy danh sách chi tiết đơn hàng theo orderId
    List<OrderItem> findByOrderOrderId(Long orderId);
}
