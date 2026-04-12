package com.loadix.application.dto.shared;

public record ApiSuccessResponse<T>(
                boolean success,
                T data) {
}
