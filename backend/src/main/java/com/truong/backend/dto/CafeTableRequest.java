package com.truong.backend.dto;

import com.truong.backend.entity.TableStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CafeTableRequest {
    private String tableNumber;
    private Integer capacity;
    private TableStatus status;

}
