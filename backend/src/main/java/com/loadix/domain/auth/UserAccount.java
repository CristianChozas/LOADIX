package com.loadix.domain.auth;

import java.util.UUID;

public record UserAccount(
        UUID id,
        String email,
        String passwordHash,
        UserRole role,
        boolean profileCompleted
) {
}
