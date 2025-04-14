package com.truong.backend.repository;

import com.truong.backend.entity.Payment;
import com.truong.backend.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Lấy danh sách thanh toán của một đơn hàng
    List<Payment> findByOrderOrderId(Long orderId);

    // Lấy danh sách thanh toán của một khách hàng (qua đơn hàng của họ)
    @Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId")
    List<Payment> findByUserId(Long userId);

    // Lấy danh sách thanh toán theo trạng thái
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    // Lấy danh sách thanh toán trong khoảng thời gian (dùng cho báo cáo Admin)
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
