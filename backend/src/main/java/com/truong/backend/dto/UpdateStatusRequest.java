package com.truong.backend.dto;

import com.truong.backend.entity.ReservationStatus;

public class UpdateStatusRequest {
    private ReservationStatus status;

    public UpdateStatusRequest() {
    }

    public UpdateStatusRequest(ReservationStatus status) {
        this.status = status;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}