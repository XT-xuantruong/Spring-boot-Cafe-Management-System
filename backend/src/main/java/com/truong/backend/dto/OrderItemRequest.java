package com.truong.backend.dto;


public class OrderItemRequest {
    // Getters and setters
    private Long itemId;
    private Integer quantity;

    public OrderItemRequest() {
    }

    public OrderItemRequest(Long itemId, Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
