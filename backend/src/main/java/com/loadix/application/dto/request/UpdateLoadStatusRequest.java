package com.loadix.application.dto.request;

import com.loadix.domain.valueobject.LoadStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateLoadStatusRequest(
    @NotNull(message = "status is required")
    LoadStatus status
) {
}
