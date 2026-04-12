package com.loadix.infrastructure.security;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loadix.application.port.out.AuthTokenService;

@Component
public class JwtAuthTokenService implements AuthTokenService {

    private final JwtTokenService jwtTokenService;

    public JwtAuthTokenService(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public String createToken(String subject, List<String> roles) {
        return jwtTokenService.createToken(subject, roles);
    }
}
