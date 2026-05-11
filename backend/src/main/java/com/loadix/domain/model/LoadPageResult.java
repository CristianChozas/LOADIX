package com.loadix.domain.model;

import java.util.List;

public record LoadPageResult(
    List<PersistedLoadPublication> items,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {
}
