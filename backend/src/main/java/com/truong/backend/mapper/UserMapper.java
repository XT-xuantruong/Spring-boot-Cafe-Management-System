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
    @Mapping(target = "id", ignore = true) // Ignore vì ID được sinh tự động
    @Mapping(target = "createdAt", ignore = true) // Ignore vì được tự động sinh bởi @CreationTimestamp
    @Mapping(target = "updatedAt", ignore = true) // Ignore vì được tự động sinh bởi @UpdateTimestamp
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(request.getPassword()))")
    User toEntity(UserRequestDTO request, @Context PasswordEncoder passwordEncoder);

    // Ánh xạ từ User sang UserResponseDTO
    UserResponseDTO toResponseDTO(User user);

    // Cập nhật User từ UserRequestDTO
    @Mapping(target = "id", ignore = true) // Không cập nhật ID
    @Mapping(target = "email", ignore = true) // Không cập nhật email
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", expression = "java(request.getPassword() != null && !request.getPassword().isEmpty() ? passwordEncoder.encode(request.getPassword()) : user.getPassword())")
    void updateEntity(@MappingTarget User user, UserRequestDTO request, @Context PasswordEncoder passwordEncoder);
}