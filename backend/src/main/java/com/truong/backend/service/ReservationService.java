package com.truong.backend.service;

import com.truong.backend.dto.ReservationResponse;
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
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CafeTableRepository cafeTableRepository;

    // Tạo đặt bàn mới (Admin, Staff, Customer)
    public ReservationResponse createReservation(Long userId, Long tableId, LocalDateTime reservationTime) {
        if (reservationTime == null) {
            throw new IllegalArgumentException("Reservation time must be provided");
        }

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
        return mapToResponseDTO(reservationRepository.save(reservation));
    }

    // Lấy danh sách đặt bàn (Admin, Staff)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Lấy đặt bàn theo ID (Admin, Staff)
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        return mapToResponseDTO(reservation);
    }

    // Lấy danh sách đặt bàn của khách hàng (Customer)
    public List<ReservationResponse> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Cập nhật trạng thái đặt bàn (Admin, Staff)
    public ReservationResponse updateReservationStatus(Long id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        reservation.setStatus(status);
        return mapToResponseDTO(reservationRepository.save(reservation));
    }

    // Hủy đặt bàn (Admin, Staff, Customer)
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    private ReservationResponse mapToResponseDTO(Reservation reservation) {
        ReservationResponse dto = new ReservationResponse();
        dto.setReservationId(reservation.getReservationId());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setStatus(reservation.getStatus());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());

        ReservationResponse.UserDTO userDTO = new ReservationResponse.UserDTO();
        userDTO.setId(reservation.getUser().getId());
        userDTO.setEmail(reservation.getUser().getEmail());
        userDTO.setName(reservation.getUser().getName());
        userDTO.setPhone(reservation.getUser().getPhone());
        userDTO.setAddress(reservation.getUser().getAddress());
        dto.setUser(userDTO);

        ReservationResponse.CafeTableDTO tableDTO = new ReservationResponse.CafeTableDTO();
        tableDTO.setTableId(reservation.getCafeTable().getTableId());
        tableDTO.setTableNumber(reservation.getCafeTable().getTableNumber());
        tableDTO.setCapacity(reservation.getCafeTable().getCapacity());
        tableDTO.setStatus(String.valueOf(reservation.getCafeTable().getStatus()));
        dto.setCafeTable(tableDTO);
        return dto;
    }
}