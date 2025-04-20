package com.truong.backend.dto;

import com.truong.backend.entity.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusRequest {
    private ReservationStatus status;
}