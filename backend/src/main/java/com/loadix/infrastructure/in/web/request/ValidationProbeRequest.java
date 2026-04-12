package com.loadix.infrastructure.in.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ValidationProbeRequest(
                @NotBlank(message = "name is required") @Size(max = 80, message = "name must contain at most 80 characters") String name,

                @NotBlank(message = "email is required") @Email(message = "email must be valid") String email) {
}
