package com.yumi.catalog.infrastructure.web;

import com.yumi.catalog.application.CategoryService;
import com.yumi.catalog.application.dto.CategoryResponse;
import com.yumi.catalog.application.dto.CategoryWithProductsResponse;
import com.yumi.shared.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService service;

  @GetMapping
  public ApiResponse<Page<CategoryResponse>> list(
      @RequestParam(required = false) String name,
      @PageableDefault(size = 20) Pageable pageable,
      HttpServletRequest request) {

    Page<CategoryResponse> page = (name != null && !name.isBlank())
        ? service.findByNameContaining(name, pageable)
        : service.findAll(pageable);

    return ApiResponse.ok(page, request);
  }

  @GetMapping("/{slug}")
  public ApiResponse<CategoryResponse> getBySlug(
      @PathVariable String slug,
      HttpServletRequest request) {
    return ApiResponse.ok(service.findBySlug(slug), request);
  }

  @GetMapping("/{slug}/products")
  public ApiResponse<CategoryWithProductsResponse> getWithProducts(
      @PathVariable String slug,
      @RequestParam(defaultValue = "10") int limit,
      HttpServletRequest request) {
    return ApiResponse.ok(service.findBySlugWithProducts(slug, limit), request);
  }
}