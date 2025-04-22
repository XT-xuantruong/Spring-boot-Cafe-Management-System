package com.truong.backend.dto;

import com.truong.backend.entity.OrderStatus;
import com.truong.backend.entity.PaymentStatus;
import com.truong.backend.entity.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class ReservationResponse {
    private Long reservationId;
    private UserDTO user;
    private CafeTableDTO cafeTable;
    private LocalDateTime reservationTime;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderDTO order; // Thêm trường để chứa thông tin đơn hàng

    public ReservationResponse() {
    }

    public ReservationResponse(Long reservationId, UserDTO user, CafeTableDTO cafeTable,
                               LocalDateTime reservationTime, ReservationStatus status,
                               LocalDateTime createdAt, LocalDateTime updatedAt, OrderDTO order) {
        this.reservationId = reservationId;
        this.user = user;
        this.cafeTable = cafeTable;
        this.reservationTime = reservationTime;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.order = order;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public CafeTableDTO getCafeTable() {
        return cafeTable;
    }

    public void setCafeTable(CafeTableDTO cafeTable) {
        this.cafeTable = cafeTable;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OrderDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDTO order) {
        this.order = order;
    }

    public static class UserDTO {
        private Long id;
        private String email;
        private String name;
        private String phone;
        private String address;

        public UserDTO() {
        }

        public UserDTO(Long id, String email, String name, String phone, String address) {
            this.id = id;
            this.email = email;
            this.name = name;
            this.phone = phone;
            this.address = address;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static class CafeTableDTO {
        private Long tableId;
        private String tableNumber;
        private int capacity;
        private String status;

        public CafeTableDTO() {
        }

        public CafeTableDTO(Long tableId, String tableNumber, int capacity, String status) {
            this.tableId = tableId;
            this.tableNumber = tableNumber;
            this.capacity = capacity;
            this.status = status;
        }

        public Long getTableId() {
            return tableId;
        }

        public void setTableId(Long tableId) {
            this.tableId = tableId;
        }

        public String getTableNumber() {
            return tableNumber;
        }

        public void setTableNumber(String tableNumber) {
            this.tableNumber = tableNumber;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class OrderDTO {
        private Long orderId;
        private Double totalAmount;
        private OrderStatus orderStatus;
        private PaymentStatus paymentStatus;

        public OrderDTO() {
        }

        public OrderDTO(Long orderId, Double totalAmount, OrderStatus orderStatus, PaymentStatus paymentStatus) {
            this.orderId = orderId;
            this.totalAmount = totalAmount;
            this.orderStatus = orderStatus;
            this.paymentStatus = paymentStatus;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public Double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public OrderStatus getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
        }

        public PaymentStatus getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
        }
    }
}