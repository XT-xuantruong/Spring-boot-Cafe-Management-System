package com.truong.backend.repository;

import com.truong.backend.entity.Order;
import com.truong.backend.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Lấy danh sách đơn hàng của một khách hàng
    List<Order> findByUserId(Long userId);

    // Lấy danh sách đơn hàng theo trạng thái
    List<Order> findByOrderStatus(OrderStatus orderStatus);

    // Lấy danh sách đơn hàng trong khoảng thời gian (dùng cho báo cáo Admin)
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Lấy danh sách đơn hàng theo reservation_id
    List<Order> findByReservationReservationId(Long reservationId);
}