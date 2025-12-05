package com.yumi.catalog.application.dto;

import java.util.List;

public record CategoryWithProductsResponse(
    Long id,
    String name,
    String slug,
    String description,
    List<ProductResponse> products,
    Long totalProducts) {
}