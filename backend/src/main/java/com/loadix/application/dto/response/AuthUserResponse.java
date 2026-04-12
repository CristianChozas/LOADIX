package com.loadix.application.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.loadix.domain.auth.UserRole;

public record AuthUserResponse(
                UUID id,
                String email,
                UserRole role,
                @JsonProperty("profile_completed") boolean profileCompleted) {
}
