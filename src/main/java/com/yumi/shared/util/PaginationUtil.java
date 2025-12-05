package com.yumi.shared.util;

import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PaginationUtil {

  private PaginationUtil() {
  }

  public static Pageable of(int page, int size, Sort.Direction direction, String... properties) {
    return PageRequest.of(Math.max(page, 0), Math.min(size, 200),
        Sort.by(Objects.requireNonNull(direction), Objects.requireNonNull(properties)));
  }

  public static Pageable of(int page, int size) {
    return of(page, size, Sort.Direction.ASC, "id");
  }
}