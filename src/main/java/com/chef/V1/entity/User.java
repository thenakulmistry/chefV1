package com.chef.V1.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    private ObjectId id;
    private String name;
    @Indexed(unique = true)
    private String username;
    @NonNull
    private String password;
    private Integer number;
    private String email;
    private String role;
    private Boolean enabled;
}
