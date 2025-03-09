package com.projectorg.dto;

import com.projectorg.entities.User;
import lombok.Data;

import java.util.Optional;

@Data
public class AuthResponse {
    private String token;
    private User user;

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
