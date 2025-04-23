package com.truong.backend.dto.request;

import com.truong.backend.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class OrderRequestDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Table ID is required")
    private Long tableId;

    private Long reservationId; // Optional, có thể null

    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;

    @NotNull(message = "Order items are required")
    @Size(min = 1, message = "At least one order item is required")
    private List<OrderItemRequestDTO> orderItems;

    // Constructors
    public OrderRequestDTO() {
    }

    public OrderRequestDTO(Long userId, Long tableId, Long reservationId, OrderStatus orderStatus, List<OrderItemRequestDTO> orderItems) {
        this.userId = userId;
        this.tableId = tableId;
        this.reservationId = reservationId;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
    }

    // Getters and Setters
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

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderItemRequestDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemRequestDTO> orderItems) {
        this.orderItems = orderItems;
    }
}