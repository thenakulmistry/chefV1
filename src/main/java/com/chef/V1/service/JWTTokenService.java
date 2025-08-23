package com.chef.V1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class JWTTokenService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String ACTIVE_PREFIX = "active:";

    public void blacklistToken(String token, long expTimeMs){
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expTimeMs, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token){
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void storeActiveToken(String userId, String token, long expTimeMs){
        String key = ACTIVE_PREFIX + userId;
        redisTemplate.opsForValue().set(key, token, expTimeMs, TimeUnit.MILLISECONDS);
    }

    public void removeActiveToken(String userId){
        String key = ACTIVE_PREFIX + userId;
        redisTemplate.delete(key);
    }

    public String getActiveToken(String userId){
        String key = ACTIVE_PREFIX + userId;
        return (String) redisTemplate.opsForValue().get(key);
    }
}
