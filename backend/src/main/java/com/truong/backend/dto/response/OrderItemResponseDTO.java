package com.truong.backend.dto.response;

public class OrderItemResponseDTO {
    private Long orderItemId;
    private Long itemId;
    private String itemName;
    private String imageUrl;
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;

    // Constructors
    public OrderItemResponseDTO() {
    }

    public OrderItemResponseDTO(Long orderItemId, Long itemId, String itemName, String imageUrl,
                                Integer quantity, Double unitPrice, Double subtotal) {
        this.orderItemId = orderItemId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    // Getters and Setters
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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