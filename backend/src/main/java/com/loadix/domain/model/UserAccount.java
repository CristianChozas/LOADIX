package com.loadix.domain.model;

import java.util.UUID;

import com.loadix.domain.valueobject.UserRole;

public record UserAccount(
        UUID id,
        String email,
        String passwordHash,
        UserRole role,
        boolean profileCompleted
) {
}
