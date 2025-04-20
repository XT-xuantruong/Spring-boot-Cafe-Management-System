package com.truong.backend.dto;

import com.truong.backend.entity.PaymentMethod;
import com.truong.backend.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
}