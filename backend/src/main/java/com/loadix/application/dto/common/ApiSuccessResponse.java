package com.loadix.application.dto.common;

public record ApiSuccessResponse<T>(
        boolean success,
        T data
) {
}
