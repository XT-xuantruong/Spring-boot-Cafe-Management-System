package com.truong.backend.dto.request;

import com.truong.backend.entity.enums.ReservationStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ReservationRequestDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Table ID is required")
    private Long tableId;

    @NotNull(message = "Reservation time is required")
    @FutureOrPresent(message = "Reservation time must be in the present or future")
    private LocalDateTime reservationTime;

    @NotNull(message = "Status is required")
    private ReservationStatus status;

    // Constructors
    public ReservationRequestDTO() {
    }

    public ReservationRequestDTO(Long userId, Long tableId, LocalDateTime reservationTime, ReservationStatus status) {
        this.userId = userId;
        this.tableId = tableId;
        this.reservationTime = reservationTime;
        this.status = status;
    }

    // Getters and Setters
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

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}