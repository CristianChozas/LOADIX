package com.loadix.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthSessionResponse", description = "Authentication session payload")
public record AuthSessionResponse(
        @Schema(description = "Authenticated user")
        AuthUserResponse user,
        @Schema(description = "Session token")
        String token
) {
}
