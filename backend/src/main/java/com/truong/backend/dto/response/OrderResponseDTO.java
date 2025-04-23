package com.truong.backend.dto.response;

import com.truong.backend.entity.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {
    private Long orderId;
    private Long userId;
    private String userName;
    private Long tableId;
    private String tableNumber;
    private Long reservationId;
    private OrderStatus orderStatus;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponseDTO> orderItems;
    private PaymentResponseDTO payment;

    // Constructors
    public OrderResponseDTO() {
    }

    public OrderResponseDTO(Long orderId, Long userId, String userName, Long tableId, String tableNumber,
                            Long reservationId, OrderStatus orderStatus, Double totalAmount,
                            LocalDateTime createdAt, LocalDateTime updatedAt,
                            List<OrderItemResponseDTO> orderItems, PaymentResponseDTO payment) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.reservationId = reservationId;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderItems = orderItems;
        this.payment = payment;
    }

    // Getters and Setters
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
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

    public List<OrderItemResponseDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemResponseDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public PaymentResponseDTO getPayment() {
        return payment;
    }

    public void setPayment(PaymentResponseDTO payment) {
        this.payment = payment;
    }
}