package com.truong.backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class UpdateProfileRequest {
    private String name;

    private String password;

    private String phone;

    private String address;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String name, String password, String phone, String address) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

}
