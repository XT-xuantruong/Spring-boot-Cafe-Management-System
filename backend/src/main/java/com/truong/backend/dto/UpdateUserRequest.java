package com.truong.backend.dto;

import com.truong.backend.entity.Role;
import lombok.Data;

@Data
public class UpdateUserRequest {
    public Long id;
    private String name;

    private String password;

    private String phone;

    private String address;

    private Role role;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String name, String password, String phone, String address, Role role) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
