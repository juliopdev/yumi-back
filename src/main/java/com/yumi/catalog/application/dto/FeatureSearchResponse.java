package com.yumi.catalog.application.dto;

import java.util.List;

public record FeatureSearchResponse(
    FeatureResponse feature,
    List<ProductResponse> products) {
}
