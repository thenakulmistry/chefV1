package com.chef.V1.repository;

import com.chef.V1.entity.PasswordResetToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, ObjectId> {
    Optional<PasswordResetToken> findByToken(String token);
}
