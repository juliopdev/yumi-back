package com.yumi.catalog.infrastructure.web;

import com.yumi.catalog.application.FeatureService;
import com.yumi.catalog.application.dto.FeatureResponse;
import com.yumi.catalog.application.dto.FeatureSearchResponse;
import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/features")
@RequiredArgsConstructor
public class FeatureController {

  private final FeatureService service;

  @GetMapping
  public ApiResponse<Page<FeatureResponse>> list(
      @RequestParam(required = false) String name,
      @PageableDefault(size = 20) Pageable pageable,
      HttpServletRequest request) {

    Page<FeatureResponse> page = (name != null && !name.isBlank())
        ? service.findByNameContaining(name, pageable)
        : service.findAll(pageable);

    return ApiResponse.ok(page, request);
  }

  @GetMapping("/{id}")
  public ApiResponse<FeatureResponse> getById(
      @PathVariable Long id,
      HttpServletRequest request) {
    return ApiResponse.ok(service.findById(id), request);
  }

  @GetMapping("/{id}/products")
  public ApiResponse<FeatureSearchResponse> getWithProducts(
      @PathVariable Long id,
      @RequestParam(defaultValue = "10") int limit,
      HttpServletRequest request) {
    return ApiResponse.ok(service.findFeatureWithProducts(id, limit), request);
  }
}