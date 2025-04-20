package com.truong.backend.dto;

import com.truong.backend.entity.OrderStatus;
import com.truong.backend.entity.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private Long tableId;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> orderItems;

    @Data
    public static class OrderItemResponse {
        private Long orderItemId;
        private Long itemId;
        private String itemName;
        private Integer quantity;
        private Double unitPrice;
        private Double subtotal;
    }
}