package com.loadix.infrastructure.in.web.response;

public record ApiSuccessResponse<T>(
                boolean success,
                T data) {
}
