package com.yumi.catalog.application.dto;

public record CategoryResponse(
    Long id,
    String name,
    String slug,
    String description,
    Boolean visible) {
}