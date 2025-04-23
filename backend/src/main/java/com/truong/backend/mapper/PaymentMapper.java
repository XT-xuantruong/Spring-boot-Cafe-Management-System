package com.truong.backend.mapper;

import com.truong.backend.dto.response.PaymentResponseDTO;
import com.truong.backend.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(source = "order.orderId", target = "orderId")
    PaymentResponseDTO toResponseDTO(Payment payment);
}