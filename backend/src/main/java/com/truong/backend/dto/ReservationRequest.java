package com.truong.backend.dto;

import java.time.LocalDateTime;

public class ReservationRequest {
    private Long userId;
    private Long tableId;
    private LocalDateTime reservationTime;

    public ReservationRequest() {
    }

    public ReservationRequest(Long userId, Long tableId, LocalDateTime reservationTime) {
        this.userId = userId;
        this.tableId = tableId;
        this.reservationTime = reservationTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

}
