//package com.truong.backend.service;
//
//import com.truong.backend.entity.Order;
//import com.truong.backend.entity.Payment;
//import com.truong.backend.entity.enums.PaymentStatus;
//import com.truong.backend.repository.OrderRepository;
//import com.truong.backend.repository.PaymentRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class PaymentService {
//
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    private PaymentResponse toResponse(Payment payment) {
//        PaymentResponse response = new PaymentResponse();
//        response.setPaymentId(payment.getPaymentId());
//        response.setOrderId(payment.getOrder().getOrderId());
//        response.setAmount(payment.getAmount());
//        response.setPaymentMethod(payment.getPaymentMethod());
//        response.setPaymentStatus(payment.getPaymentStatus());
//        response.setPaymentTime(payment.getPaymentTime());
//        response.setTransactionId(payment.getTransactionId());
//        return response;
//    }
//
//    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
//        Order order = orderRepository.findById(paymentRequest.getOrderId())
//                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + paymentRequest.getOrderId()));
//
//        // Kiểm tra xem Order đã có Payment chưa
//        if (order.getPayment() != null) {
//            throw new IllegalArgumentException("Order already has a Payment. Each Order can only have one Payment.");
//        }
//
//        if (paymentRepository.existsByTransactionId(paymentRequest.getTransactionId())) {
//            throw new IllegalArgumentException("Transaction ID '" + paymentRequest.getTransactionId() + "' already exists");
//        }
//
//        Payment payment = new Payment();
//        payment.setOrder(order);
//        payment.setAmount(paymentRequest.getAmount());
//        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
//        payment.setPaymentStatus(PaymentStatus.PAID);
//        payment.setPaymentTime(LocalDateTime.now());
//        payment.setTransactionId(paymentRequest.getTransactionId());
//
//        Payment savedPayment;
//        try {
//            savedPayment = paymentRepository.save(payment);
//        } catch (DataIntegrityViolationException e) {
//            throw new IllegalArgumentException("Transaction ID '" + paymentRequest.getTransactionId() + "' already exists");
//        }
//
//        // Liên kết Payment với Order và đồng bộ paymentStatus
//        order.setPayment(savedPayment);
//        order.setPaymentStatus(savedPayment.getPaymentStatus());
//        orderRepository.save(order);
//
//        return toResponse(savedPayment);
//    }
//
//    public List<PaymentResponse> getAllPayments() {
//        return paymentRepository.findAll().stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//    }
//
//    public List<PaymentResponse> getPaymentsByOrder(Long orderId) {
//        Payment payment = paymentRepository.findByOrderOrderId(orderId)
//                .orElse(null);
//        return payment != null ? List.of(toResponse(payment)) : List.of();
//    }
//
//    public List<PaymentResponse> getPaymentsByUser(Long userId) {
//        return paymentRepository.findByUserId(userId).stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//    }
//
//    public PaymentResponse updatePaymentStatus(Long id, UpdatePaymentRequest updatePaymentRequest) {
//        Payment payment = paymentRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + id));
//        payment.setPaymentStatus(updatePaymentRequest.getPaymentStatus());
//        Payment updatedPayment = paymentRepository.save(payment);
//
//        // Đồng bộ Order.paymentStatus với Payment.paymentStatus
//        Order order = payment.getOrder();
//        order.setPaymentStatus(updatedPayment.getPaymentStatus());
//        orderRepository.save(order);
//
//        return toResponse(updatedPayment);
//    }
//}