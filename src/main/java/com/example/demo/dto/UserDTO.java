package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String username;
    private String name;
    private String email;

    public UserDTO() {
    }

    public UserDTO(String username, String name, String email) {
        this.username = username;
        this.name = name;
        this.email = email;
    }
}
