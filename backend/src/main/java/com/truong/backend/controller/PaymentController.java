package com.truong.backend.controller;

import com.truong.backend.dto.request.PaymentRequestDTO;
import com.truong.backend.dto.response.ApiResponse;
import com.truong.backend.dto.response.PaymentResponseDTO;
import com.truong.backend.entity.enums.PaymentStatus;
import com.truong.backend.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getAllPayments() {
        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(new ApiResponse<>("success", "Payments retrieved successfully", payments));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getPaymentsByOrder(@PathVariable Long orderId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByOrder(orderId);
        return ResponseEntity.ok(new ApiResponse<>("success", "Payment retrieved successfully", payments));
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getPaymentsByUser(@RequestParam Long userId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByUser(userId);
        return ResponseEntity.ok(new ApiResponse<>("success", "User payments retrieved successfully", payments));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> processPayment(@RequestBody @Valid PaymentRequestDTO paymentRequest) {
        try {
            PaymentResponseDTO response = paymentService.processPayment(paymentRequest);
            return ResponseEntity.ok(new ApiResponse<>("success", "Payment processed successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>("error", e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody @Valid String paymentStatus) {
        try {
            PaymentResponseDTO response = paymentService.updatePaymentStatus(id, PaymentStatus.valueOf(paymentStatus));
            return ResponseEntity.ok(new ApiResponse<>("success", "Payment status updated successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", e.getMessage(), null));
        }
    }
}