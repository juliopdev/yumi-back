package com.yumi.catalog.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeatureRequest(
    @NotBlank @Size(max = 100) String name,

    @NotBlank @Size(max = 255) String value) {
}