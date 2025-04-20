package com.truong.backend.dto;

import com.truong.backend.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String role;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.role = user.getRole().name();
    }

    public UserResponse() {
    }

}