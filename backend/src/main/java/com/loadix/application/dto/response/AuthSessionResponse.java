package com.loadix.application.dto.response;

public record AuthSessionResponse(
        AuthUserResponse user,
        String token
) {
}
