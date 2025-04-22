package com.truong.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MenuItemRequest {
    @NotNull(message = "Tên món ăn là bắt buộc")
    private String itemName;

    private String description;

    @NotNull(message = "Giá là bắt buộc")
    @Positive(message = "Giá phải lớn hơn 0")
    private Double price;


    public MenuItemRequest() {
    }

    public MenuItemRequest(String itemName, String description, Double price) {
        this.itemName = itemName;
        this.description = description;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}