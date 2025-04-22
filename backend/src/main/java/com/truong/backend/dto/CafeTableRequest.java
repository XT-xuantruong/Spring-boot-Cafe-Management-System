package com.truong.backend.dto;

import com.truong.backend.entity.TableStatus;

public class CafeTableRequest {
    private String tableNumber;
    private Integer capacity;
    private TableStatus status;

    public CafeTableRequest() {
    }

    public CafeTableRequest(TableStatus status, Integer capacity, String tableNumber) {
        this.status = status;
        this.capacity = capacity;
        this.tableNumber = tableNumber;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }
}
