package com.saran.ratelimiter.limiter;

import java.util.concurrent.atomic.AtomicInteger;

public class TokenBucketLimiter implements RateLimiter {

    private final int capacity;
    private final int refillRatePerSecond;

    private final AtomicInteger tokens;
    private long lastRefillTimeMillis;

    public TokenBucketLimiter(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.tokens = new AtomicInteger(capacity);
        this.lastRefillTimeMillis = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean allowRequest(String key) {
        // key is unused for now (in-memory version)
        refillTokens();

        if (tokens.get() > 0) {
            tokens.decrementAndGet();
            return true;
        }
        return false;
    }

    private void refillTokens() {
        long now = System.currentTimeMillis();
        long elapsedMillis = now - lastRefillTimeMillis;

        if (elapsedMillis > 0) {
            int tokensToAdd =
                    (int) ((elapsedMillis / 1000.0) * refillRatePerSecond);

            if (tokensToAdd > 0) {
                int newCount = Math.min(capacity, tokens.get() + tokensToAdd);
                tokens.set(newCount);
                lastRefillTimeMillis = now;
            }
        }
    }
}
