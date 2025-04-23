package com.truong.backend.service;

import com.truong.backend.dto.response.ReservationResponseDTO;
import com.truong.backend.entity.*;
import com.truong.backend.entity.enums.OrderStatus;
import com.truong.backend.entity.enums.PaymentStatus;
import com.truong.backend.entity.enums.ReservationStatus;
import com.truong.backend.entity.enums.TableStatus;
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

    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapWithOrder)
                .collect(Collectors.toList());
    }

    public ReservationResponseDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        return mapWithOrder(reservation);
    }

    public List<ReservationResponseDTO> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::mapWithOrder)
                .collect(Collectors.toList());
    }

    public ReservationResponseDTO createReservation(Authentication authentication, Long tableId, LocalDateTime reservationTime) {
        if (reservationTime == null) {
            throw new IllegalArgumentException("Reservation time must be provided");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        CafeTable table = cafeTableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Table not found with ID: " + tableId));

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
        Reservation savedReservation = reservationRepository.save(reservation);
        return mapWithOrder(savedReservation);
    }

    public ReservationResponseDTO updateReservationStatus(Long id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        reservation.setStatus(status);
        if (status == ReservationStatus.CANCELLED) {
            cafeTableService.updateTableStatus(reservation.getCafeTable().getTableId(), TableStatus.AVAILABLE);
        }
        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapWithOrder(updatedReservation);
    }

    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        reservation.setStatus(ReservationStatus.CANCELLED);
        List<Order> relatedOrders = orderRepository.findByReservationReservationId(id);
        relatedOrders.forEach(order -> {
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.getPayment().setPaymentStatus(PaymentStatus.CANCELLED);
            order.setReservation(null);
            cafeTableService.updateTableStatus(order.getTable().getTableId(), TableStatus.AVAILABLE);
            orderRepository.save(order);
        });
        cafeTableService.updateTableStatus(reservation.getCafeTable().getTableId(), TableStatus.AVAILABLE);
        reservationRepository.save(reservation);
    }

    private ReservationResponseDTO mapWithOrder(Reservation reservation) {
        ReservationResponseDTO dto = reservationMapper.toResponseDTO(reservation);
        List<Order> relatedOrders = orderRepository.findByReservationReservationId(reservation.getReservationId());
        if (!relatedOrders.isEmpty()) {
            Order order = relatedOrders.get(0); // Giả định 1 Reservation có tối đa 1 Order
            dto.setOrderId(order.getOrderId());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setOrderStatus(order.getOrderStatus());
        }
        return dto;
    }
}