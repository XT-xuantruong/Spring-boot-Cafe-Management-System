package com.truong.backend.dto;

import com.truong.backend.entity.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationResponse {
    private Long reservationId;
    private UserDTO user;
    private CafeTableDTO cafeTable;
    private LocalDateTime reservationTime;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    public static class UserDTO {
        private Long id;
        private String email;
        private String name;
        private String phone;
        private String address;
    }

    @Getter
    @Setter
    public static class CafeTableDTO {
        private Long tableId;
        private String tableNumber;
        private int capacity;
        private String status;
    }
}