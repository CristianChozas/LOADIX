package com.loadix.infrastructure.in.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValidationProbeRequest", description = "Sample payload used to verify validation handling")
public record ValidationProbeRequest(
                @Schema(description = "Person name", example = "Maria")
                @NotBlank(message = "name is required") @Size(max = 80, message = "name must contain at most 80 characters") String name,

                @Schema(description = "Email address", example = "maria@loadix.test")
                @NotBlank(message = "email is required") @Email(message = "email must be valid") String email) {
}
