package com.truong.backend.controller;

import com.truong.backend.entity.Reservation;
import com.truong.backend.entity.ReservationStatus;
import com.truong.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // Lấy danh sách đặt bàn (Admin, Staff)
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    // Lấy đặt bàn theo ID (Admin, Staff)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    // Lấy danh sách đặt bàn của khách hàng (Customer)
    @GetMapping("/customer")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<List<Reservation>> getReservationsByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }

    // Tạo đặt bàn mới (Admin, Staff, Customer)
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_CUSTOMER')")
    public ResponseEntity<Reservation> createReservation(
            @RequestParam Long userId,
            @RequestParam Long tableId,
            @RequestParam String reservationTime) {
        LocalDateTime time = LocalDateTime.parse(reservationTime);
        return ResponseEntity.ok(reservationService.createReservation(userId, tableId, time));
    }

    // Cập nhật trạng thái đặt bàn (Admin, Staff)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<Reservation> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, status));
    }

    // Hủy đặt bàn (Admin, Staff, Customer)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_CUSTOMER')")
    public ResponseEntity<String> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok("Reservation cancelled");
    }
}
