package com.saran.ratelimiter.filter;

import com.saran.ratelimiter.service.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    public RateLimitFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String clientId = request.getHeader("X-CLIENT-ID");

        if (clientId == null || clientId.isBlank()) {
            response.sendError(400, "Missing X-CLIENT-ID header");
            return;
        }

        boolean allowed = rateLimiterService.isAllowed(clientId);

        if (!allowed) {
            response.setHeader("Retry-After", "1");
            response.sendError(429, "RATE LIMIT EXCEEDED");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
