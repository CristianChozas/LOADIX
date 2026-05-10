package com.loadix.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UpdateEmailRequest", description = "Payload used to update the authenticated user's email")
public record UpdateEmailRequest(
        @Schema(description = "New email address", example = "new-email@loadix.test")
        @NotBlank @Email String email
) {
}
