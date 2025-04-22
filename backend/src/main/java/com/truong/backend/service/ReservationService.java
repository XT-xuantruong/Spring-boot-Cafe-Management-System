package com.truong.backend.service;

import com.truong.backend.dto.response.ReservationResponseDTO;
import com.truong.backend.entity.*;
import com.truong.backend.mapper.ReservationMapper;
import com.truong.backend.repository.CafeTableRepository;
import com.truong.backend.repository.OrderRepository;
import com.truong.backend.repository.ReservationRepository;
import com.truong.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CafeTableRepository cafeTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CafeTableService cafeTableService;

    @Autowired
    private ReservationMapper reservationMapper;

    // Lấy tất cả đặt bàn
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Lấy đặt bàn theo ID
    public ReservationResponseDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        return reservationMapper.toResponseDTO(reservation);
    }

    // Lấy danh sách đặt bàn của một người dùng
    public List<ReservationResponseDTO> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(reservationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Tạo đặt bàn mới (Admin, Staff, Customer)
    public ReservationResponseDTO createReservation(
            Authentication authentication,
            Long tableId,
            LocalDateTime reservationTime
    ) {
        if (reservationTime == null) {
            throw new IllegalArgumentException("Reservation time must be provided");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
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

        cafeTableService.updateTableStatus(table.getTableId(), TableStatus.RESERVED);
        return reservationMapper.toResponseDTO(reservationRepository.save(reservation));
    }

    // Cập nhật trạng thái đặt bàn
    public ReservationResponseDTO updateReservationStatus(
            Long id,
            ReservationStatus status
    ) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        reservation.setStatus(status);
        if (reservation.getStatus().equals(ReservationStatus.CANCELLED)) {
            cafeTableService.updateTableStatus(reservation.getCafeTable().getTableId(), TableStatus.AVAILABLE);
        }
        return reservationMapper.toResponseDTO(reservationRepository.save(reservation));
    }

    // Hủy đặt bàn
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        reservation.setStatus(ReservationStatus.CANCELLED);
        List<Order> relatedOrders = orderRepository.findByReservationReservationId(id);
        relatedOrders.forEach(order -> {
            order.setReservation(null);
            orderRepository.save(order);
        });
        cafeTableService.updateTableStatus(
                reservation.getCafeTable().getTableId(),
                TableStatus.AVAILABLE
        );
        reservationRepository.save(reservation);
    }
}