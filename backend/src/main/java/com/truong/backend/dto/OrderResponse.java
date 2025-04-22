package com.truong.backend.dto;

import com.truong.backend.entity.OrderStatus;
import com.truong.backend.entity.PaymentStatus;
import com.truong.backend.entity.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long orderId;
    private Long userId;
    private Long tableId;
    private Long reservationId; // Thêm reservationId
    private ReservationResponse reservation; // Thêm thông tin reservation
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> orderItems;

    public OrderResponse() {
    }

    public OrderResponse(Long orderId, Long userId, Long tableId, Long reservationId, ReservationResponse reservation,
                         OrderStatus orderStatus, PaymentStatus paymentStatus, Double totalAmount,
                         LocalDateTime createdAt, LocalDateTime updatedAt, List<OrderItemResponse> orderItems) {
        this.orderId = orderId;
        this.userId = userId;
        this.tableId = tableId;
        this.reservationId = reservationId;
        this.reservation = reservation;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderItems = orderItems;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public ReservationResponse getReservation() {
        return reservation;
    }

    public void setReservation(ReservationResponse reservation) {
        this.reservation = reservation;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItemResponse> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemResponse> orderItems) {
        this.orderItems = orderItems;
    }

    public static class OrderItemResponse {
        private Long orderItemId;
        private Long itemId;
        private String itemName;
        private Integer quantity;
        private Double unitPrice;
        private Double subtotal;

        public OrderItemResponse() {
        }

        public OrderItemResponse(Long orderItemId, Long itemId, String itemName, Integer quantity,
                                 Double unitPrice, Double subtotal) {
            this.orderItemId = orderItemId;
            this.itemId = itemId;
            this.itemName = itemName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
        }

        public Long getOrderItemId() {
            return orderItemId;
        }

        public void setOrderItemId(Long orderItemId) {
            this.orderItemId = orderItemId;
        }

        public Long getItemId() {
            return itemId;
        }

        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(Double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(Double subtotal) {
            this.subtotal = subtotal;
        }
    }

    public static class ReservationResponse {
        private Long reservationId;
        private LocalDateTime reservationTime;
        private ReservationStatus status;

        public ReservationResponse() {
        }

        public ReservationResponse(Long reservationId, LocalDateTime reservationTime, ReservationStatus status) {
            this.reservationId = reservationId;
            this.reservationTime = reservationTime;
            this.status = status;
        }

        public Long getReservationId() {
            return reservationId;
        }

        public void setReservationId(Long reservationId) {
            this.reservationId = reservationId;
        }

        public LocalDateTime getReservationTime() {
            return reservationTime;
        }

        public void setReservationTime(LocalDateTime reservationTime) {
            this.reservationTime = reservationTime;
        }

        public ReservationStatus getStatus() {
            return status;
        }

        public void setStatus(ReservationStatus status) {
            this.status = status;
        }
    }
}