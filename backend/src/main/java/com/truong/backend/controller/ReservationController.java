package com.truong.backend.controller;

import com.truong.backend.dto.request.ReservationRequestDTO;
import com.truong.backend.dto.response.ApiResponse;
import com.truong.backend.dto.response.ReservationResponseDTO;
import com.truong.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<List<ReservationResponseDTO>>> getAllReservations() {
        List<ReservationResponseDTO> reservations =
                reservationService.getAllReservations();
        return ResponseEntity.ok(
                new ApiResponse<>("success", "getAllReservations", reservations)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> getReservationById(
            @PathVariable Long id)
    {
        ReservationResponseDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(
                new ApiResponse<>("success", "getReservationById", reservation)
        );
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<List<ReservationResponseDTO>>> getReservationsByUser(
            @RequestParam Long userId
    ) {
        List<ReservationResponseDTO> reservations = reservationService.getReservationsByUser(userId);
        return ResponseEntity.ok(
                new ApiResponse<>("success", "getReservationsByUser", reservations)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> createReservation(
            @RequestBody ReservationRequestDTO request,
            Authentication authentication)
    {
        ReservationResponseDTO reservationResponse =reservationService.createReservation(
                authentication,
                request.getTableId(),
                request.getReservationTime()
        );
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "success",
                        "createReservation",
                        reservationResponse
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> updateReservationStatus(
            @PathVariable Long id,
            @RequestBody ReservationRequestDTO request)
    {
        ReservationResponseDTO reservationResponse =
                reservationService.updateReservationStatus(id, request.getStatus());
        return ResponseEntity.ok(
                new ApiResponse<>("success", "updateReservationStatus", reservationResponse)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<String>> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok(
                new ApiResponse<>("success", "cancelReservation", null)
        );
    }
}