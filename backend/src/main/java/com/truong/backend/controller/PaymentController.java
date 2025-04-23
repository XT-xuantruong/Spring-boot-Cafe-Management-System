//package com.truong.backend.controller;
//
//import com.truong.backend.service.PaymentService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/payments")
//public class PaymentController {
//
//    @Autowired
//    private PaymentService paymentService;
//
//    @GetMapping
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
//    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
//        return ResponseEntity.ok(paymentService.getAllPayments());
//    }
//
//    @GetMapping("/order/{orderId}")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_CUSTOMER')")
//    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrder(@PathVariable Long orderId) {
//        return ResponseEntity.ok(paymentService.getPaymentsByOrder(orderId));
//    }
//
//    @GetMapping("/customer")
//    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
//    public ResponseEntity<List<PaymentResponse>> getPaymentsByUser(@RequestParam Long userId) {
//        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
//    }
//
//    @PostMapping
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
//    public ResponseEntity<PaymentResponse> processPayment(@RequestBody @Valid PaymentRequest paymentRequest) {
//        PaymentResponse response = paymentService.processPayment(paymentRequest);
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
//    public ResponseEntity<PaymentResponse> updatePaymentStatus(
//            @PathVariable Long id,
//            @RequestBody @Valid UpdatePaymentRequest updatePaymentRequest) {
//        PaymentResponse response = paymentService.updatePaymentStatus(id, updatePaymentRequest);
//        return ResponseEntity.ok(response);
//    }
//}