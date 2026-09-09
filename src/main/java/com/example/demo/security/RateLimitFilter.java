package com.example.demo.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private static final Map<String, Long> RATE_LIMITS = Map.of(
            "POST:/api/auth/login", 5L,
            "POST:/api/users/register", 10L,
            "GET:/api/users", 100L
    );
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket(long capacity, Duration duration) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.greedy(capacity, duration));
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket resolveBucket(String key, long capacity, Duration duration) {
        return buckets.computeIfAbsent(key, k -> createBucket(capacity, duration));
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String endpointKey = request.getMethod() + ":" + request.getRequestURI();
        long limit = RATE_LIMITS.getOrDefault(endpointKey, 200L);
        String bucketKey = request.getRemoteAddr() + ":" + endpointKey;
        Bucket bucket = resolveBucket(bucketKey, limit, Duration.ofMinutes(1));
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "status": 429,
                    "message": "Too many requests"
                }
                """);
        }
    }
}
