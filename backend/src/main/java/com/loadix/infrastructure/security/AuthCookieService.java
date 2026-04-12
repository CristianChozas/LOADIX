package com.loadix.infrastructure.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.loadix.infrastructure.config.SecurityProperties;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthCookieService {

    public static final String AUTH_COOKIE_NAME = "LOADIX_AUTH";

    private final SecurityProperties securityProperties;

    public AuthCookieService(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public void setAuthCookie(HttpServletResponse response, String token) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(token, securityProperties.jwtExpiration().toSeconds()).toString());
    }

    public void clearAuthCookie(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie("", 0).toString());
    }

    private ResponseCookie buildCookie(String value, long maxAgeSeconds) {
        return ResponseCookie.from(AUTH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(securityProperties.cookieSecure())
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }
}
