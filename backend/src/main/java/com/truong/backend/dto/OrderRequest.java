package com.truong.backend.dto;

import java.util.List;

public class OrderRequest {
    private Long tableId;
    private Long reservationId; // Thêm trường reservationId
    private List<OrderItemRequest> items;

    public OrderRequest() {
    }

    public OrderRequest(Long tableId, Long reservationId, List<OrderItemRequest> items) {
        this.tableId = tableId;
        this.reservationId = reservationId;
        this.items = items;
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

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}