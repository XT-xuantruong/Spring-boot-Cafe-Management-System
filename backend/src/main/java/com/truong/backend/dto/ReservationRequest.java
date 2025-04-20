package com.truong.backend.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationRequest {
    private Long userId;
    private Long tableId;
    private LocalDateTime reservationTime;
}
