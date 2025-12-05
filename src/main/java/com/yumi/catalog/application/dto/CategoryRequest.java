package com.yumi.catalog.application.dto;

import jakarta.validation.constraints.*;

public record CategoryRequest(
    @NotBlank @Size(min = 3, max = 100) String name,

    @Size(max = 500) String description) {
}