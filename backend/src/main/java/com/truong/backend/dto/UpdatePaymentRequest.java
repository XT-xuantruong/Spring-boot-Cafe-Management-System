package com.truong.backend.dto;

import com.truong.backend.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdatePaymentRequest {
    @NotNull(message = "Trạng thái thanh toán là bắt buộc")
    private PaymentStatus paymentStatus;
}