package com.truong.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class OrderRequest {
    @NotNull(message = "Table ID is required")
    private Long tableId;

    @NotEmpty(message = "Items list cannot be empty")
    private List<OrderItemRequest> items;

    public OrderRequest() {
    }

    public OrderRequest(Long tableId, List<OrderItemRequest> items) {
        this.tableId = tableId;
        this.items = items;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}