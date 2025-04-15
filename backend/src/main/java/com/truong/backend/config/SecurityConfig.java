package com.truong.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(UserDetailsService userDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/users/profile").authenticated()
                        .requestMatchers("/api/users").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").authenticated()
                        // Table endpoints
                        .requestMatchers("/api/tables").hasAnyRole("ADMIN", "STAFF") // Admin và Staff xem danh sách bàn
                        .requestMatchers("/api/tables/**").hasAnyRole("ADMIN", "STAFF") // Admin và Staff xem/cập nhật/xóa bàn
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/tables").hasRole("ADMIN") // Chỉ Admin tạo bàn
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/tables/**").hasRole("ADMIN") // Chỉ Admin cập nhật bàn
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/tables/**").hasRole("ADMIN") // Chỉ Admin xóa bàn

                        // Reservation endpoints
                        .requestMatchers("/api/reservations").hasAnyRole("ADMIN", "STAFF") // Admin và Staff xem danh sách đặt bàn
                        .requestMatchers("/api/reservations/**").hasAnyRole("ADMIN", "STAFF") // Admin và Staff xem chi tiết đặt bàn
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/reservations").hasAnyRole("ADMIN", "STAFF", "CUSTOMER") // Tất cả tạo đặt bàn
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/reservations/**").hasAnyRole("ADMIN", "STAFF") // Admin và Staff cập nhật trạng thái
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/reservations/**").hasAnyRole("ADMIN", "STAFF", "CUSTOMER") // Tất cả hủy đặt bàn
                        .requestMatchers("/api/reservations/customer").hasRole("CUSTOMER") // Customer xem danh sách đặt bàn của mình

                        // Menu Item endpoints
                        .requestMatchers("/api/menu-items").permitAll() // Tất cả xem danh sách món
                        .requestMatchers("/api/menu-items/**").permitAll() // Tất cả xem chi tiết món
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/menu-items").hasRole("ADMIN") // Chỉ Admin tạo món
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/menu-items/**").hasRole("ADMIN") // Chỉ Admin cập nhật món
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/menu-items/**").hasRole("ADMIN") // Chỉ Admin xóa món

                        // Order endpoints
                        .requestMatchers("/api/orders").hasAnyRole("ADMIN", "STAFF") // Admin và Staff xem danh sách đơn hàng
                        .requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "STAFF") // Admin và Staff xem chi tiết đơn hàng
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/orders").hasAnyRole("ADMIN", "STAFF", "CUSTOMER") // Tất cả tạo đơn hàng
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/orders/**").hasAnyRole("ADMIN", "STAFF") // Admin và Staff cập nhật trạng thái
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/orders/**").hasAnyRole("ADMIN", "STAFF") // Admin và Staff hủy đơn hàng
                        .requestMatchers("/api/orders/customer").hasRole("CUSTOMER") // Customer xem danh sách đơn hàng của mình

                        // Payment endpoints
                        .requestMatchers("/api/payments").hasAnyRole("ADMIN", "STAFF") // Admin và Staff xem danh sách thanh toán
                        .requestMatchers("/api/payments/order/**").hasAnyRole("ADMIN", "STAFF", "CUSTOMER") // Tất cả xem thanh toán của đơn hàng
                        .requestMatchers("/api/payments/customer").hasRole("CUSTOMER") // Customer xem danh sách thanh toán của mình
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/payments").hasAnyRole("ADMIN", "STAFF") // Admin và Staff xử lý thanh toán
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/payments/**").hasAnyRole("ADMIN", "STAFF") // Admin và Staff cập nhật trạng thái thanh toán
                        .anyRequest().authenticated())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://127.0.0.1:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}