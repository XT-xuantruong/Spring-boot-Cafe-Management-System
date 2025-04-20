package com.truong.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemRequest {
    // Getters and setters
    private Long itemId;
    private Integer quantity;

}
