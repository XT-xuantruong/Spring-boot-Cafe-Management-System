package com.truong.backend.service;

import com.truong.backend.dto.request.OrderItemRequestDTO;
import com.truong.backend.dto.request.OrderRequestDTO;
import com.truong.backend.dto.response.OrderResponseDTO;
import com.truong.backend.entity.*;
import com.truong.backend.entity.enums.OrderStatus;
import com.truong.backend.entity.enums.PaymentStatus;
import com.truong.backend.entity.enums.ReservationStatus;
import com.truong.backend.entity.enums.TableStatus;
import com.truong.backend.mapper.OrderMapper;
import com.truong.backend.repository.CafeTableRepository;
import com.truong.backend.repository.OrderItemRepository;
import com.truong.backend.repository.OrderRepository;
import com.truong.backend.repository.ReservationRepository;
import com.truong.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CafeTableRepository cafeTableRepository;

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CafeTableService cafeTableService;

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        // Validate user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getUserId()));

        // Validate table
        CafeTable table = cafeTableRepository.findById(request.getTableId())
                .orElseThrow(() -> new IllegalArgumentException("Table not found with ID: " + request.getTableId()));
        if (table.getStatus() == TableStatus.OCCUPIED) {
            throw new IllegalArgumentException("Table " + table.getTableNumber() + " is currently occupied");
        }

        // Validate reservation (if provided)
        Reservation reservation = null;
        if (request.getReservationId() != null) {
            reservation = reservationRepository.findById(request.getReservationId())
                    .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + request.getReservationId()));
            if (!reservation.getUser().getId().equals(request.getUserId()) ||
                    !reservation.getCafeTable().getTableId().equals(request.getTableId())) {
                throw new IllegalArgumentException("Reservation does not match user or table");
            }
            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                throw new IllegalArgumentException("Cannot create order for a cancelled reservation");
            }
        }

        // Validate order items
        if (request.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setTable(table);
        order.setReservation(reservation);
        order.setOrderStatus(request.getOrderStatus());

        // Process order items
        double totalAmount = 0.0;
        for (OrderItemRequestDTO itemRequest : request.getOrderItems()) {
            MenuItem menuItem = menuItemService.getMenuItemById(itemRequest.getItemId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(menuItem.getPrice());
            orderItem.setSubtotal(menuItem.getPrice() * itemRequest.getQuantity());
            order.getOrderItems().add(orderItem);
            totalAmount += orderItem.getSubtotal();
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Update table status
        cafeTableService.updateTableStatus(table.getTableId(), TableStatus.OCCUPIED);

        return orderMapper.toResponseDTO(savedOrder);
    }

    public List<OrderResponseDTO> getAllOrders() {
        return orderMapper.toResponseDTOList(orderRepository.findAll());
    }

    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        return orderMapper.toResponseDTO(order);
    }

    public List<OrderResponseDTO> getOrdersByUser(Long userId) {
        return orderMapper.toResponseDTOList(orderRepository.findByUserId(userId));
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        order.setOrderStatus(status);
        if (status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED) {
            cafeTableService.updateTableStatus(order.getTable().getTableId(), TableStatus.AVAILABLE);
        }
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(updatedOrder);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.getPayment().setPaymentStatus(PaymentStatus.CANCELLED);
        if (order.getReservation() != null) {
            Reservation reservation = order.getReservation();
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
        }
        cafeTableService.updateTableStatus(order.getTable().getTableId(), TableStatus.AVAILABLE);
        orderRepository.save(order);
    }
}