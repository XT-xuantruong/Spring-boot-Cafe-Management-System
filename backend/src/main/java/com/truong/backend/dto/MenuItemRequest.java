package com.truong.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MenuItemRequest {
    @NotNull(message = "Tên món ăn là bắt buộc")
    private String itemName;

    private String description;

    @NotNull(message = "Giá là bắt buộc")
    @Positive(message = "Giá phải lớn hơn 0")
    private Double price;

    private String category;

    private Boolean isAvailable;

    private String imageUrl;
}