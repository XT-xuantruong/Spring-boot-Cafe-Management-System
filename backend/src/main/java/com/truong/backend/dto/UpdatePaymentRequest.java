package com.truong.backend.dto;

import com.truong.backend.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public class UpdatePaymentRequest {
    @NotNull(message = "Trạng thái thanh toán là bắt buộc")
    private PaymentStatus paymentStatus;

    public UpdatePaymentRequest(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public UpdatePaymentRequest() {
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}