package com.loadix.infrastructure.http.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startedAt = System.currentTimeMillis();
        String requestId = resolveRequestId(request);
        String pathWithQuery = buildPathWithQuery(request);
        String clientIp = resolveClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        response.setHeader(REQUEST_ID_HEADER, requestId);

        LOGGER.info(
                "HTTP_REQUEST id={} method={} path={} ip={} user_agent=\"{}\"",
                requestId,
                request.getMethod(),
                pathWithQuery,
                clientIp,
                sanitize(userAgent)
        );

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startedAt;
            String authenticatedUser = resolveAuthenticatedUser();
            LOGGER.info(
                    "HTTP_RESPONSE id={} method={} path={} status={} duration_ms={} user={}",
                    requestId,
                    request.getMethod(),
                    pathWithQuery,
                    response.getStatus(),
                    durationMs,
                    authenticatedUser
            );
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String incoming = request.getHeader(REQUEST_ID_HEADER);
        if (incoming != null && !incoming.isBlank()) {
            return incoming.trim();
        }
        return UUID.randomUUID().toString();
    }

    private String buildPathWithQuery(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query == null || query.isBlank()) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + query;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String resolveAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return "anonymous";
        }
        return authentication.getName();
    }

    private String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "n/a";
        }
        return value.replace('"', '\'');
    }
}
