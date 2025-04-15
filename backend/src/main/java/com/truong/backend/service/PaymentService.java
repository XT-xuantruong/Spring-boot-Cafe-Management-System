package com.truong.backend.service;

import com.truong.backend.entity.Order;
import com.truong.backend.entity.Payment;
import com.truong.backend.entity.PaymentMethod;
import com.truong.backend.entity.PaymentStatus;
import com.truong.backend.repository.OrderRepository;
import com.truong.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Xử lý thanh toán mới (Admin, Staff)
    public Payment processPayment(Long orderId, Double amount, PaymentMethod paymentMethod, String transactionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setTransactionId(transactionId);

        Payment savedPayment = paymentRepository.save(payment);

        // Cập nhật trạng thái thanh toán của đơn hàng
        double totalPaid = order.getPayments().stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PAID)
                .mapToDouble(Payment::getAmount)
                .sum();
        if (totalPaid >= order.getTotalAmount()) {
            order.setPaymentStatus(PaymentStatus.PAID);
        } else if (totalPaid > 0) {
            order.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
        }
        orderRepository.save(order);

        return savedPayment;
    }

    // Lấy danh sách thanh toán (Admin, Staff)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Lấy danh sách thanh toán của một đơn hàng (Admin, Staff, Customer)
    public List<Payment> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByOrderOrderId(orderId);
    }

    // Lấy danh sách thanh toán của khách hàng (Customer)
    public List<Payment> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    // Cập nhật trạng thái thanh toán (Admin, Staff)
    public Payment updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + id));
        payment.setPaymentStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);

        // Cập nhật trạng thái thanh toán của đơn hàng
        Order order = payment.getOrder();
        double totalPaid = order.getPayments().stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PAID)
                .mapToDouble(Payment::getAmount)
                .sum();
        if (totalPaid >= order.getTotalAmount()) {
            order.setPaymentStatus(PaymentStatus.PAID);
        } else if (totalPaid > 0) {
            order.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
        } else {
            order.setPaymentStatus(PaymentStatus.UNPAID);
        }
        orderRepository.save(order);

        return updatedPayment;
    }
}
