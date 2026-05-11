package com.loadix.application.dto.response;

import java.util.List;

public record MyLoadsPageResponse(
    List<LoadResponse> items,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {
}
