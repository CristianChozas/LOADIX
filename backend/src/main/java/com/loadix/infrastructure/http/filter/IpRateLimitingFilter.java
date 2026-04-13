package com.loadix.infrastructure.http.filter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.loadix.infrastructure.config.RateLimitProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class IpRateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitProperties properties;
    private final ConcurrentHashMap<String, FixedWindowBucket> buckets = new ConcurrentHashMap<>();

    public IpRateLimitingFilter(RateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        RateLimitRule rule = resolveRule(request);
        if (rule == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = request.getRemoteAddr() + ":" + rule.key;
        FixedWindowBucket bucket = buckets.computeIfAbsent(
                key,
                ignored -> new FixedWindowBucket(rule.capacity, rule.window)
        );

        if (!bucket.tryConsume()) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Too many requests for this endpoint");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private RateLimitRule resolveRule(HttpServletRequest request) {
        String path = request.getRequestURI();

        if (path.startsWith("/api/v1/auth")) {
            return new RateLimitRule("auth", properties.authCapacity(), properties.authWindow());
        }

        if (HttpMethod.GET.matches(request.getMethod()) && path.equals("/api/v1/health")) {
            return new RateLimitRule("public", properties.publicCapacity(), properties.publicWindow());
        }

        return null;
    }

    private record RateLimitRule(String key, int capacity, Duration window) {
    }

    private static final class FixedWindowBucket {
        private final int capacity;
        private final Duration window;
        private int remaining;
        private Instant windowStart;

        private FixedWindowBucket(int capacity, Duration window) {
            this.capacity = capacity;
            this.window = window;
            this.remaining = capacity;
            this.windowStart = Instant.now();
        }

        private synchronized boolean tryConsume() {
            Instant now = Instant.now();

            if (now.isAfter(windowStart.plus(window))) {
                windowStart = now;
                remaining = capacity;
            }

            if (remaining <= 0) {
                return false;
            }

            remaining--;
            return true;
        }
    }
}
