package com.truong.backend.config;

import com.truong.backend.entity.enums.Role;
import com.truong.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.truong.backend.entity.User;
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem tài khoản admin đã tồn tại chưa
        if (!userRepository.findByEmail("root@admin.com").isPresent()) {
            User admin = new User();
            admin.setEmail("root@admin.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Tài khoản admin đã được tạo!");
        } else {
            System.out.println("Tài khoản admin đã tồn tại!");
        }
    }
}
