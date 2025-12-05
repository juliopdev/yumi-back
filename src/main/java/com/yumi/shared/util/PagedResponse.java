package com.yumi.shared.util;

import java.util.List;

public record PagedResponse<T>(
    List<T> content,
    int totalPages,
    long totalElements,
    int size,
    int number) {
}