package com.saran.ratelimiter.limiter;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisTokenBucketLimiter implements RateLimiter {

    private final RedisTemplate<String, String> redisTemplate;
    private final int capacity;
    private final int refillRatePerSecond;

    public RedisTokenBucketLimiter(
            RedisTemplate<String, String> redisTemplate,
            int capacity,
            int refillRatePerSecond) {

        this.redisTemplate = redisTemplate;
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    @Override
    public synchronized boolean allowRequest(String key) {

        System.out.println(">>> REDIS LIMITER HIT for key = " + key);


        String tokensKey = "rate:tokens:" + key;
        String timeKey = "rate:timestamp:" + key;

        long now = System.currentTimeMillis();

        String tokensStr = redisTemplate.opsForValue().get(tokensKey);
        String timeStr = redisTemplate.opsForValue().get(timeKey);

        int tokens;
        long lastRefillTime;

        if (tokensStr == null || timeStr == null) {
            tokens = capacity;
            lastRefillTime = now;
        } else {
            tokens = Integer.parseInt(tokensStr);
            lastRefillTime = Long.parseLong(timeStr);
        }

        long elapsedMillis = now - lastRefillTime;
        int tokensToAdd =
                (int) ((elapsedMillis / 1000.0) * refillRatePerSecond);

        if (tokensToAdd > 0) {
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTime = now;
        }

        if (tokens > 0) {
            tokens--;

            redisTemplate.opsForValue().set(tokensKey, String.valueOf(tokens));
            redisTemplate.opsForValue().set(timeKey, String.valueOf(lastRefillTime));
            return true;
        }

        redisTemplate.opsForValue().set(tokensKey, String.valueOf(tokens));
        redisTemplate.opsForValue().set(timeKey, String.valueOf(lastRefillTime));

        return false;
    }
}
