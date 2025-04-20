package com.truong.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrderRequest {
    @NotNull(message = "Table ID is required")
    private Long tableId;

    @NotEmpty(message = "Items list cannot be empty")
    private List<OrderItemRequest> items;
}