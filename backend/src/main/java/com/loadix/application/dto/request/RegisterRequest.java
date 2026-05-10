package com.loadix.application.dto.request;

import com.loadix.domain.valueobject.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RegisterRequest", description = "Payload used to register a new user")
public record RegisterRequest(
        @Schema(description = "User email", example = "carrier@loadix.test")
        @NotBlank @Email String email,
        @Schema(description = "Raw password", example = "Password1")
        @NotBlank @Size(min = 8, max = 72) String password,
        @Schema(description = "Account role", example = "CARRIER")
        @NotNull UserRole role) {
}
