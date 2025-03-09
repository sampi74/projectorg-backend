package com.projectorg.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private String username;
    private String password;
    private String userFullName;
    private String userEmail;

    public UserDto(String userFullName, String userEmail) {
        this.userFullName = userFullName;
        this.userEmail = userEmail;
    }
}
