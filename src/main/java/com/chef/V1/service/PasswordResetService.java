package com.chef.V1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RESET_TOKEN_PREFIX = "reset_token:";
    private static final String VERIFY_TOKEN_PREFIX = "verify_token:";
    private static final String RATE_LIMIT_PREFIX = "reset_rate:";

    private static final long RESET_TOKEN_EXPIRY_MINUTES = 10; // 10 minutes
    private static final long VERIFY_TOKEN_EXPIRY_MINUTES = 30; // 30 minutes
    private static final long RATE_LIMIT_MINUTES = 1; // 1 minute between requests

    public void storeResetToken(String token, String email) {
        String key = RESET_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, email, RESET_TOKEN_EXPIRY_MINUTES, TimeUnit.MINUTES);
    }

    public String getEmailFromResetToken(String token) {
        String key = RESET_TOKEN_PREFIX + token;
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteResetToken(String token) {
        String key = RESET_TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }

    public void storeVerifyToken(String token, String email) {
        String key = VERIFY_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, email, VERIFY_TOKEN_EXPIRY_MINUTES, TimeUnit.MINUTES);
    }

    public String getEmailFromVerifyToken(String token) {
        String key = VERIFY_TOKEN_PREFIX + token;
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteVerifyToken(String token) {
        String key = VERIFY_TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }

    public boolean isRateLimited(String email) {
        String key = RATE_LIMIT_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setRateLimit(String email) {
        String key = RATE_LIMIT_PREFIX + email;
        redisTemplate.opsForValue().set(key, "limited", RATE_LIMIT_MINUTES, TimeUnit.MINUTES);
    }
}
