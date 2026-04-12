package com.loadix.application.dto.auth;

import java.util.UUID;

import com.loadix.domain.auth.UserRole;

public record AuthUserResponse(
        UUID id,
        String email,
        UserRole role,
        boolean profileCompleted
) {
}
