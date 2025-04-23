package com.truong.backend.service;

import com.truong.backend.dto.request.PaymentRequestDTO;
import com.truong.backend.dto.response.PaymentResponseDTO;
import com.truong.backend.entity.Order;
import com.truong.backend.entity.Payment;
import com.truong.backend.entity.enums.PaymentStatus;
import com.truong.backend.mapper.PaymentMapper;
import com.truong.backend.repository.OrderRepository;
import com.truong.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {
        // Validate Order
        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + paymentRequest.getOrderId()));

        // Check if Order already has a Payment
        if (paymentRepository.findByOrderOrderId(paymentRequest.getOrderId()).isPresent()) {
            throw new IllegalStateException("Order already has a Payment. Each Order can only have one Payment.");
        }

        // Validate transactionId for ONLINE payment
        if (paymentRequest.getPaymentMethod() == com.truong.backend.entity.enums.PaymentMethod.ONLINE &&
                (paymentRequest.getTransactionId() == null || paymentRequest.getTransactionId().trim().isEmpty())) {
            throw new IllegalArgumentException("Transaction ID is required for ONLINE payment method");
        }

        // Check if transactionId is unique
        if (paymentRequest.getTransactionId() != null && paymentRepository.existsByTransactionId(paymentRequest.getTransactionId())) {
            throw new IllegalStateException("Transaction ID '" + paymentRequest.getTransactionId() + "' already exists");
        }

        // Create Payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PAID); // Default to PAID
        payment.setPaymentTime(LocalDateTime.now());
        payment.setTransactionId(paymentRequest.getTransactionId());

        // Save Payment
        Payment savedPayment;
        try {
            savedPayment = paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Failed to save payment due to data integrity violation: " + e.getMessage());
        }

        orderRepository.save(order);

        return paymentMapper.toResponseDTO(savedPayment);
    }

    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentResponseDTO> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByOrderOrderId(orderId)
                .map(paymentMapper::toResponseDTO)
                .map(List::of)
                .orElse(List.of());
    }

    public List<PaymentResponseDTO> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(paymentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponseDTO updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + id));
        payment.setPaymentStatus(paymentStatus);
        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponseDTO(updatedPayment);
    }
}