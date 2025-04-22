package com.truong.backend.dto;

import com.truong.backend.entity.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private OrderStatus status;

    public UpdateOrderStatusRequest(OrderStatus status) {
        this.status = status;
    }

    public UpdateOrderStatusRequest() {
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}