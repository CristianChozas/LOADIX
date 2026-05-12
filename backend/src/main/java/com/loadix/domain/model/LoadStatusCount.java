package com.loadix.domain.model;

import com.loadix.domain.valueobject.LoadStatus;

public record LoadStatusCount(
    LoadStatus status,
    long count
) {
}
