package com.truong.backend.controller;

import com.truong.backend.dto.OrderRequest;
import com.truong.backend.dto.OrderResponse;
import com.truong.backend.dto.UpdateOrderStatusRequest;
import com.truong.backend.entity.OrderStatus;
import com.truong.backend.entity.User;
import com.truong.backend.repository.UserRepository;
import com.truong.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository  userRepository;

    // Lấy danh sách đơn hàng (Admin, Staff)
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Lấy đơn hàng theo ID (Admin, Staff)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // Lấy danh sách đơn hàng của khách hàng (Customer)
    @GetMapping("/customer")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return ResponseEntity.ok(orderService.getOrdersByUser(user.getId()));
    }

    // Tạo đơn hàng mới (Admin, Staff, Customer)
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_CUSTOMER')")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        return ResponseEntity.ok(orderService.createOrder(
                user.getId(),
                request.getTableId(),
                request.getItems()
        ));
    }


    // Cập nhật trạng thái đơn hàng (Admin, Staff)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status.getStatus()));
    }

    // Hủy đơn hàng (Admin, Staff)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order cancelled");
    }
}