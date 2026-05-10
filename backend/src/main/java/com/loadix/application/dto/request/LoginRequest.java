package com.loadix.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginRequest", description = "Payload used to authenticate a user")
public record LoginRequest(
        @Schema(description = "User email", example = "carrier@loadix.test")
        @NotBlank @Email String email,
        @Schema(description = "Raw password", example = "Password1")
        @NotBlank String password) {
}
