package com.yumi.catalog.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
    Long id,
    String sku,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    Boolean visible,
    String imageUrl,
    CategoryResponse category,
    List<FeatureResponse> features) {
}