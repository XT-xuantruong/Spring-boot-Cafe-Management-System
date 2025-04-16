package com.truong.backend.service;

import com.truong.backend.entity.CafeTable;
import com.truong.backend.entity.Reservation;
import com.truong.backend.entity.ReservationStatus;
import com.truong.backend.entity.User;
import com.truong.backend.repository.CafeTableRepository;
import com.truong.backend.repository.ReservationRepository;
import com.truong.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CafeTableRepository cafeTableRepository;

    // Tạo đặt bàn mới (Admin, Staff, Customer)
    public Reservation createReservation(Long userId, Long tableId, LocalDateTime reservationTime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        CafeTable table = cafeTableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Table not found with ID: " + tableId));

        // Kiểm tra bàn đã được đặt vào thời điểm đó chưa
        List<Reservation> existingReservations = reservationRepository.findByTableIdAndReservationTime(tableId, reservationTime);
        if (!existingReservations.isEmpty()) {
            throw new IllegalArgumentException("Table is already reserved at this time");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setCafeTable(table);
        reservation.setReservationTime(reservationTime);
        reservation.setStatus(ReservationStatus.PENDING);
        return reservationRepository.save(reservation);
    }

    // Lấy danh sách đặt bàn (Admin, Staff)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Lấy đặt bàn theo ID (Admin, Staff)
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
    }

    // Lấy danh sách đặt bàn của khách hàng (Customer)
    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    // Cập nhật trạng thái đặt bàn (Admin, Staff)
    public Reservation updateReservationStatus(Long id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }

    // Hủy đặt bàn (Admin, Staff, Customer)
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }
}
