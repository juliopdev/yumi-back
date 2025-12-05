package com.yumi.catalog.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record CreateProductRequest(
    @NotBlank @Size(min = 8, max = 8) String sku,

    @NotBlank @Size(min = 3, max = 100) String name,

    @Size(max = 1000) String description,

    @NotNull @Positive BigDecimal price,

    @NotNull @PositiveOrZero Integer stock,

    @NotNull Long categoryId,

    @NotNull MultipartFile image,

    @NotEmpty @Size(max = 20) List<@Valid FeatureRequest> features) {
}