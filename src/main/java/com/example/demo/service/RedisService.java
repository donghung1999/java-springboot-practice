package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "blacklist:";

    public void blacklistToken(String token, long expirationMillis) {
        redisTemplate.opsForValue().set(
                PREFIX + token,
                "1",
                expirationMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public boolean isBlacklistedToken(String token) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(PREFIX + token)
        );
    }
}
