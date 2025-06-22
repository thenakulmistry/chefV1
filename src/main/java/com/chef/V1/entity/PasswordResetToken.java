package com.chef.V1.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "reset_tokens")
@Data
@NoArgsConstructor
public class PasswordResetToken {
    @Id
    private ObjectId id;
    private String token;
    @DBRef
    private User user;
    private static final int EXPIRATION_SECONDS = 600;
    private Instant expiryDate;

    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = Instant.now().plusSeconds(EXPIRATION_SECONDS);
    }

    public boolean isExpired(){
        return Instant.now().isAfter(expiryDate);
    }
}
