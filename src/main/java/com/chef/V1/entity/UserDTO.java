package com.chef.V1.entity;

import lombok.Data;

@Data
public class UserDTO {
    private String name;
    private String username;
    private String password;
    private String email;
    private Integer number;
}
