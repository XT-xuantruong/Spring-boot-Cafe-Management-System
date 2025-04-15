package com.truong.backend.controller;

import com.truong.backend.entity.Payment;
import com.truong.backend.entity.PaymentMethod;
import com.truong.backend.entity.PaymentStatus;
import com.truong.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Lấy danh sách thanh toán (Admin, Staff)
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // Lấy danh sách thanh toán của một đơn hàng (Admin, Staff, Customer)
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_CUSTOMER')")
    public ResponseEntity<List<Payment>> getPaymentsByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrder(orderId));
    }

    // Lấy danh sách thanh toán của khách hàng (Customer)
    @GetMapping("/customer")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<List<Payment>> getPaymentsByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
    }

    // Xử lý thanh toán mới (Admin, Staff)
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<Payment> processPayment(
            @RequestParam Long orderId,
            @RequestParam Double amount,
            @RequestParam PaymentMethod paymentMethod,
            @RequestParam String transactionId) {
        return ResponseEntity.ok(paymentService.processPayment(orderId, amount, paymentMethod, transactionId));
    }

    // Cập nhật trạng thái thanh toán (Admin, Staff)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(id, status));
    }
}
