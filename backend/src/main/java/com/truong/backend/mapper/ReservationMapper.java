package com.truong.backend.mapper;

import com.truong.backend.dto.response.ReservationResponseDTO;
import com.truong.backend.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "cafeTable.tableId", target = "tableId")
    @Mapping(source = "cafeTable.tableNumber", target = "tableNumber")
    @Mapping(source = "reservationTime", target = "reservationTime")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "orderId", ignore = true) // Sẽ được set trong ReservationService
    @Mapping(target = "totalAmount", ignore = true) // Sẽ được set trong ReservationService
    @Mapping(target = "orderStatus", ignore = true) // Sẽ được set trong ReservationService
    ReservationResponseDTO toResponseDTO(Reservation reservation);
}