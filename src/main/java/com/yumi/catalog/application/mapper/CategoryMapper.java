package com.yumi.catalog.application.mapper;

import com.yumi.catalog.application.dto.CategoryResponse;
import com.yumi.catalog.application.dto.CategoryWithProductsResponse;
import com.yumi.catalog.application.dto.ProductResponse;
import com.yumi.catalog.domain.Category;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Transformaciones entre Category y sus DTOs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryMapper {

  public static CategoryResponse toResponse(Category c) {
    return new CategoryResponse(
        c.getId(),
        c.getName(),
        c.getSlug(),
        c.getDescription(),
        c.getVisible());
  }

  public static CategoryWithProductsResponse toResponseWithProducts(Category c,
      List<ProductResponse> products,
      long totalProducts) {
    return new CategoryWithProductsResponse(
        c.getId(),
        c.getName(),
        c.getSlug(),
        c.getDescription(),
        products,
        totalProducts);
  }
}