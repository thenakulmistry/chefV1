package com.chef.V1.entity;

import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "users")
@Data
public class User {
    @Id
    private ObjectId id;
    private String name;
    private int number;
    private String email;
    @NonNull
    private String password;
    private String role;
}
