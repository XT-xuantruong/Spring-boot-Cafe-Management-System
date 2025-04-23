package com.truong.backend.service;

import com.truong.backend.dto.request.UserProfileDTO;
import com.truong.backend.dto.request.UserRequestDTO;
import com.truong.backend.dto.response.UserResponseDTO;
import com.truong.backend.entity.User;
import com.truong.backend.mapper.UserMapper;
import com.truong.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            CloudinaryService cloudinaryService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
    }

    // Lấy tất cả người dùng
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Lấy người dùng theo ID
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toResponseDTO(user);
    }

    // Xóa người dùng
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

    // Lấy hồ sơ người dùng hiện tại
    public UserResponseDTO getCurrentUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return userMapper.toResponseDTO(user);
    }

    // Tạo người dùng mới
    public UserResponseDTO createUser(UserRequestDTO request) {
        // Kiểm tra xem email đã tồn tại chưa
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Ánh xạ DTO sang Entity
        User user = userMapper.toEntity(request, passwordEncoder);

        // Lưu vào database
        User savedUser = userRepository.save(user);

        // Ánh xạ sang ResponseDTO
        return userMapper.toResponseDTO(savedUser);
    }

    // Cập nhật người dùng theo ID
    public UserResponseDTO userUpdate(Long id, UserRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        // Cập nhật Entity từ DTO
        userMapper.updateEntity(user, request, passwordEncoder);

        // Lưu vào database
        User updatedUser = userRepository.save(user);

        // Ánh xạ sang ResponseDTO
        return userMapper.toResponseDTO(updatedUser);
    }

    // Cập nhật hồ sơ người dùng hiện tại
    public UserResponseDTO updateUserProfile(UserProfileDTO request, MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        if (file != null && !file.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(file, "avatar");
           user.setAvatar_url(imageUrl);
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        // Lưu vào database
        User updatedUser = userRepository.save(user);

        // Ánh xạ sang ResponseDTO
        return userMapper.toResponseDTO(updatedUser);
    }
}