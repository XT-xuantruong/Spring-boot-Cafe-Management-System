package com.truong.backend.repository;

import com.truong.backend.entity.Payment;
import com.truong.backend.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderOrderId(Long orderId); // Thay đổi từ List<Payment> thành Optional<Payment>

    @Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId")
    List<Payment> findByUserId(Long userId);

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByTransactionId(String transactionId);
}