package com.chef.V1.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String name;
    private String username;
    private String password;
    private String email;
    private Integer number;
    private Boolean enabled;
    private String usernameOrEmail; // Add this field for login
}
