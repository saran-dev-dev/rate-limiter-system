package com.saran.ratelimiter.service;

import com.saran.ratelimiter.limiter.RedisTokenBucketLimiter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private final RedisTokenBucketLimiter limiter;

    public RateLimiterService(RedisTemplate<String, String> redisTemplate) {
        System.out.println(">>> USING REDIS RATE LIMITER");
        this.limiter = new RedisTokenBucketLimiter(redisTemplate, 5, 1);
    }

    public boolean isAllowed(String clientId) {
        return limiter.allowRequest(clientId);
    }
}
