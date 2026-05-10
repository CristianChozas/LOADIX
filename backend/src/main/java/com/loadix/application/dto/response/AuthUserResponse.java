package com.loadix.application.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.loadix.domain.valueobject.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthUserResponse", description = "Authenticated user payload")
public record AuthUserResponse(
                @Schema(description = "User identifier")
                UUID id,
                @Schema(description = "Email address")
                String email,
                @Schema(description = "User role")
                UserRole role,
                @Schema(description = "Whether the profile is completed")
                @JsonProperty("profile_completed") boolean profileCompleted) {
}
