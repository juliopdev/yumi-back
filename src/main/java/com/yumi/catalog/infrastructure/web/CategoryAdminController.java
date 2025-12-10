package com.yumi.catalog.infrastructure.web;

import com.yumi.catalog.application.CategoryService;
import com.yumi.catalog.application.dto.CategoryRequest;
import com.yumi.catalog.application.dto.CategoryResponse;
import com.yumi.shared.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','INVENTORYMANAGER')")
public class CategoryAdminController {

  private final CategoryService service;

  @PostMapping
  public ApiResponse<CategoryResponse> create(
      @Valid @RequestBody CategoryRequest req,
      HttpServletRequest request) {
    return ApiResponse.ok(service.create(req), request);
  }

  @PutMapping("/{slug}")
  public ApiResponse<CategoryResponse> update(
      @PathVariable String slug,
      @Valid @RequestBody CategoryRequest req,
      HttpServletRequest request) {
    return ApiResponse.ok(service.update(slug, req), request);
  }

  @DeleteMapping("/{slug}")
  public ApiResponse<String> delete(
      @PathVariable String slug,
      HttpServletRequest request) {
    service.delete(slug);
    return ApiResponse.ok("Categoría eliminada exitosamente", request);
  }
}