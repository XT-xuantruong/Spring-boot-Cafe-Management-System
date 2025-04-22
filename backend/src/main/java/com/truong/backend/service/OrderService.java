package com.truong.backend.service;

import com.truong.backend.dto.OrderItemRequest;
import com.truong.backend.dto.OrderResponse;
import com.truong.backend.entity.*;
import com.truong.backend.repository.CafeTableRepository;
import com.truong.backend.repository.OrderItemRepository;
import com.truong.backend.repository.OrderRepository;
import com.truong.backend.repository.ReservationRepository;
import com.truong.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    // Tạo đơn hàng mới (Admin, Staff, Customer)
    public OrderResponse createOrder(Long userId, Long tableId, Long reservationId, List<OrderItemRequest> items) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        CafeTable table = cafeTableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Table not found with ID: " + tableId));

        Reservation reservation = null;
        if (reservationId != null) {
            reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + reservationId));
            // Kiểm tra reservation có thuộc về user và table không
            if (!reservation.getUser().getId().equals(userId) || !reservation.getCafeTable().getTableId().equals(tableId)) {
                throw new IllegalArgumentException("Reservation does not match user or table");
            }
            // Kiểm tra trạng thái reservation
            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                throw new IllegalArgumentException("Cannot create order for a cancelled reservation");
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setTable(table);
        order.setReservation(reservation);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID);

        double totalAmount = 0.0;
        for (OrderItemRequest itemRequest : items) {
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
        return mapToOrderResponse(savedOrder);
    }

    // Lấy danh sách đơn hàng (Admin, Staff)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    // Lấy đơn hàng theo ID (Admin, Staff)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        return mapToOrderResponse(order);
    }

    // Lấy danh sách đơn hàng của khách hàng (Customer)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    // Cập nhật trạng thái đơn hàng (Admin, Staff)
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        order.setOrderStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    // Hủy đơn hàng (Admin, Staff)
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.CANCELLED);
        // Nếu đơn hàng liên quan đến reservation, có thể cập nhật trạng thái reservation (tuỳ nghiệp vụ)
        if (order.getReservation() != null) {
            Reservation reservation = order.getReservation();
            reservation.setStatus(ReservationStatus.CANCELLED); // Ví dụ: hủy cả reservation
            reservationRepository.save(reservation);
        }
        orderRepository.save(order);
    }

    // Hàm chuyển đổi từ Order sang OrderResponse
    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUser().getId());
        response.setTableId(order.getTable().getTableId());
        response.setReservationId(order.getReservation() != null ? order.getReservation().getReservationId() : null);
        response.setOrderStatus(order.getOrderStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> {
                    OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
                    itemResponse.setOrderItemId(item.getOrderItemId());
                    itemResponse.setItemId(item.getItem().getItemId());
                    itemResponse.setItemName(item.getItem().getItemName());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setUnitPrice(item.getUnitPrice());
                    itemResponse.setSubtotal(item.getSubtotal());
                    return itemResponse;
                })
                .collect(Collectors.toList());
        response.setOrderItems(itemResponses);

        // Thêm thông tin reservation nếu có
        if (order.getReservation() != null) {
            OrderResponse.ReservationResponse reservationResponse = new OrderResponse.ReservationResponse();
            reservationResponse.setReservationId(order.getReservation().getReservationId());
            reservationResponse.setReservationTime(order.getReservation().getReservationTime());
            reservationResponse.setStatus(order.getReservation().getStatus());
            response.setReservation(reservationResponse);
        }

        return response;
    }

    public OrderItemRepository getOrderItemRepository() {
        return orderItemRepository;
    }

    public void setOrderItemRepository(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }
}