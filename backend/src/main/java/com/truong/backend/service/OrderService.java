package com.truong.backend.service;

import com.truong.backend.dto.OrderItemRequest;
import com.truong.backend.entity.MenuItem;
import com.truong.backend.entity.OrderStatus;
import com.truong.backend.entity.PaymentStatus;
import com.truong.backend.entity.User;
import com.truong.backend.entity.Order;
import com.truong.backend.entity.OrderItem;
import com.truong.backend.entity.CafeTable;
import com.truong.backend.repository.CafeTableRepository;
import com.truong.backend.repository.OrderItemRepository;
import com.truong.backend.repository.OrderRepository;
import com.truong.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Tạo đơn hàng mới (Admin, Staff, Customer)
    public Order createOrder(Long userId, Long tableId, List<OrderItemRequest> items) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        CafeTable table = cafeTableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Table not found with ID: " + tableId));

        Order order = new Order();
        order.setUser(user);
        order.setTable(table);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID);

        double totalAmount = 0.0;
        for (OrderItemRequest itemRequest : items) {
            MenuItem menuItem = menuItemService.getMenuItemById(itemRequest.getItemId());
            if (!menuItem.getIsAvailable()) {
                throw new IllegalArgumentException("Menu item not available: " + menuItem.getItemName());
            }

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
        return orderRepository.save(order);
    }

    // Lấy danh sách đơn hàng (Admin, Staff)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Lấy đơn hàng theo ID (Admin, Staff)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
    }

    // Lấy danh sách đơn hàng của khách hàng (Customer)
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserUserId(userId);
    }

    // Cập nhật trạng thái đơn hàng (Admin, Staff)
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    // Hủy đơn hàng (Admin, Staff)
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.CANCELLED);
        orderRepository.save(order);
    }
}
