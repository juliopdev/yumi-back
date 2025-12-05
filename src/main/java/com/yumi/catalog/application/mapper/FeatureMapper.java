package com.yumi.catalog.application.mapper;

import com.yumi.catalog.application.dto.FeatureResponse;
import com.yumi.catalog.domain.Feature;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Transformaciones entre Feature y sus DTOs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureMapper {

  public static FeatureResponse toResponse(Feature f) {
    return new FeatureResponse(f.getId(), f.getName(), f.getValue());
  }
}