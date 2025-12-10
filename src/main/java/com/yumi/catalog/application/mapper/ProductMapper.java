package com.yumi.catalog.application.mapper;

import com.yumi.catalog.application.dto.*;
import com.yumi.catalog.domain.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Transformaciones entre Product y sus DTOs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductMapper {

  public static ProductResponse toResponse(Product p) {
    return new ProductResponse(
        p.getId(),
        p.getSku(),
        p.getName(),
        p.getDescription(),
        p.getPrice(),
        p.getStock(),
        p.getVisible(),
        p.getImageUrl(),
        CategoryMapper.toResponse(p.getCategory()),
        p.getFeatures().stream().map(FeatureMapper::toResponse).toList());
  }

  public static List<ProductResponse> toResponseList(List<Product> products) {
    return products.stream().map(ProductMapper::toResponse).toList();
  }
}