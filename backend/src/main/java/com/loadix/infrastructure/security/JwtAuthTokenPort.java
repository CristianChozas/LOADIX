package com.loadix.infrastructure.security;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loadix.application.port.out.AuthTokenPort;

@Component
public class JwtAuthTokenPort implements AuthTokenPort {

    private final JwtTokenService jwtTokenService;

    public JwtAuthTokenPort(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public String createToken(String subject, List<String> roles) {
        return jwtTokenService.createToken(subject, roles);
    }
}
