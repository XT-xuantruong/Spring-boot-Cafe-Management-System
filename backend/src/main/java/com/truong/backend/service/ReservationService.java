package com.truong.backend.service;

import com.truong.backend.dto.ReservationResponse;
import com.truong.backend.entity.CafeTable;
import com.truong.backend.entity.Order;
import com.truong.backend.entity.Reservation;
import com.truong.backend.entity.ReservationStatus;
import com.truong.backend.entity.User;
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

    // Tạo đặt bàn mới (Admin, Staff, Customer)
    public ReservationResponse createReservation(Authentication authentication, Long tableId, LocalDateTime reservationTime) {
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
        return mapToResponseDTO(reservationRepository.save(reservation));
    }

    // Các phương thức khác giữ nguyên
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        return mapToResponseDTO(reservation);
    }

    public List<ReservationResponse> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ReservationResponse updateReservationStatus(Long id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        reservation.setStatus(status);
        return mapToResponseDTO(reservationRepository.save(reservation));
    }

    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        reservation.setStatus(ReservationStatus.CANCELLED);
        List<Order> relatedOrders = orderRepository.findByReservationReservationId(id);
        relatedOrders.forEach(order -> {
            order.setReservation(null);
            orderRepository.save(order);
        });
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

        List<Order> relatedOrders = orderRepository.findByReservationReservationId(reservation.getReservationId());
        if (!relatedOrders.isEmpty()) {
            ReservationResponse.OrderDTO orderDTO = new ReservationResponse.OrderDTO();
            orderDTO.setOrderId(relatedOrders.get(0).getOrderId());
            orderDTO.setTotalAmount(relatedOrders.get(0).getTotalAmount());
            orderDTO.setOrderStatus(relatedOrders.get(0).getOrderStatus());
            orderDTO.setPaymentStatus(relatedOrders.get(0).getPaymentStatus());
            dto.setOrder(orderDTO);
        }

        return dto;
    }
}