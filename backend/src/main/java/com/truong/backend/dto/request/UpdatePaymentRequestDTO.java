package com.truong.backend.dto.request;

import com.truong.backend.entity.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public class UpdatePaymentRequestDTO {
    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    // Constructors
    public UpdatePaymentRequestDTO() {
    }

    public UpdatePaymentRequestDTO(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}