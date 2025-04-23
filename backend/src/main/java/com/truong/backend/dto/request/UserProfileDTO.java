package com.truong.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UserProfileDTO {
    @NotBlank(message = "Name is required")
    private String name;

    private String phone;

    private String address;

    // Constructors
    public UserProfileDTO() {
    }

    public UserProfileDTO(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}