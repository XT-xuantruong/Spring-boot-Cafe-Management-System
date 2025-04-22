package com.truong.backend.dto;

import com.truong.backend.entity.PaymentMethod;
import com.truong.backend.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentRequest {
    @NotNull(message = "ID đơn hàng là bắt buộc")
    private Long orderId;

    @NotNull(message = "Số tiền là bắt buộc")
    @Positive(message = "Số tiền phải lớn hơn 0")
    private Double amount;

    @NotNull(message = "Phương thức thanh toán là bắt buộc")
    private PaymentMethod paymentMethod;

    @NotNull(message = "ID giao dịch là bắt buộc")
    private String transactionId;

    private PaymentStatus paymentStatus; // Dùng cho PUT

    public PaymentRequest() {}

    public PaymentRequest(Long orderId, Double amount, PaymentMethod paymentMethod, String transactionId, PaymentStatus paymentStatus) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paymentStatus = paymentStatus;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}