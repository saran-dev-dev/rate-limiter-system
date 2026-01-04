package com.saran.ratelimiter.limiter;

public interface RateLimiter {
    boolean allowRequest(String key);
}
