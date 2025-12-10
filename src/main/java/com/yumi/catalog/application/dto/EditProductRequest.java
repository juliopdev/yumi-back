package com.yumi.catalog.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record EditProductRequest(
    @Size(min = 8, max = 8) String sku,

    @Size(min = 3, max = 100) String name,

    @Size(max = 1000) String description,

    @Positive BigDecimal price,

    @PositiveOrZero Integer stock,

    Long categoryId,

    MultipartFile image,

    @Size(max = 20) List<@Valid FeatureRequest> features) {
}