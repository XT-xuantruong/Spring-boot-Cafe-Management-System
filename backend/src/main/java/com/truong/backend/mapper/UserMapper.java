package com.truong.backend.mapper;

import com.truong.backend.dto.request.UserRequestDTO;
import com.truong.backend.dto.response.UserResponseDTO;
import com.truong.backend.entity.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Ánh xạ từ UserRequestDTO sang User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "avatar_url", source = "avatarUrl") // Ánh xạ avatarUrl sang avatar_url
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(request.getPassword()))")
    User toEntity(UserRequestDTO request, @Context PasswordEncoder passwordEncoder);

    // Ánh xạ từ User sang UserResponseDTO
    @Mapping(source = "avatar_url", target = "avatarUrl")
    UserResponseDTO toResponseDTO(User user);

    // Cập nhật User từ UserRequestDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "avatar_url", source = "avatarUrl")
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "password", expression = "java(request.getPassword() != null && !request.getPassword().isEmpty() ? passwordEncoder.encode(request.getPassword()) : user.getPassword())")
    void updateEntity(@MappingTarget User user, UserRequestDTO request, @Context PasswordEncoder passwordEncoder);
}