package com.truong.backend.mapper;

import com.truong.backend.dto.response.OrderItemResponseDTO;
import com.truong.backend.dto.response.OrderResponseDTO;
import com.truong.backend.entity.Order;
import com.truong.backend.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = { PaymentMapper.class })
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "table.tableId", target = "tableId")
    @Mapping(source = "table.tableNumber", target = "tableNumber")
    @Mapping(source = "reservation.reservationId", target = "reservationId")
    @Mapping(source = "orderItems", target = "orderItems")
    OrderResponseDTO toResponseDTO(Order order);

    List<OrderResponseDTO> toResponseDTOList(List<Order> orders);

    @Mapping(source = "item.itemId", target = "itemId")
    @Mapping(source = "item.itemName", target = "itemName")
    @Mapping(source = "item.imageUrl", target = "imageUrl")
    OrderItemResponseDTO toItemResponseDTO(OrderItem orderItem);

    List<OrderItemResponseDTO> toItemResponseDTOList(List<OrderItem> orderItems);
}