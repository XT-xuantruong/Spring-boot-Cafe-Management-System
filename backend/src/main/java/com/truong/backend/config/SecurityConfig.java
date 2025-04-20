package com.truong.backend.config;

import com.truong.backend.entity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final Log logger = LogFactory.getLog(User.class);

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/users/profile").authenticated()
                        .requestMatchers("/api/users").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")
                        // Table endpoints
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/tables").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/tables/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/tables/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/tables").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                        .requestMatchers("/api/tables/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                        // Reservation endpoints
                        .requestMatchers("/api/reservations/customer").hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/reservations").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF", "ROLE_CUSTOMER")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/reservations/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/reservations/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF", "ROLE_CUSTOMER")
                        .requestMatchers("/api/reservations").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                        .requestMatchers("/api/reservations/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")


                        // Menu Item endpoints

                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/menu-items").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/menu-items/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/menu-items/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/menu-items").permitAll()
                        .requestMatchers("/api/menu-items/**").permitAll()
                        // Order endpoints
                        .requestMatchers("/api/orders/customer").hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/orders").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF", "ROLE_CUSTOMER")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/orders/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/orders/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                        .requestMatchers("/api/orders").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                        .requestMatchers("/api/orders/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")

                        // Payment endpoints
                        .requestMatchers("/api/payments").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                        .requestMatchers("/api/payments/order/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF", "ROLE_CUSTOMER")
                        .requestMatchers("/api/payments/customer").hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/payments").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/payments/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
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