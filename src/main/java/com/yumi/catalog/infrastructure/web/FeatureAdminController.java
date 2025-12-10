package com.yumi.catalog.infrastructure.web;

import com.yumi.catalog.application.FeatureService;
import com.yumi.catalog.application.dto.FeatureRequest;
import com.yumi.catalog.application.dto.FeatureResponse;
import com.yumi.shared.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/features")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','INVENTORYMANAGER')")
public class FeatureAdminController {

  private final FeatureService service;

  @PostMapping
  public ApiResponse<FeatureResponse> create(
      @Valid @RequestBody FeatureRequest req,
      HttpServletRequest request) {
    return ApiResponse.ok(service.create(req), request);
  }

  @PutMapping("/{id}")
  public ApiResponse<FeatureResponse> update(
      @PathVariable Long id,
      @Valid @RequestBody FeatureRequest req,
      HttpServletRequest request) {
    return ApiResponse.ok(service.update(id, req), request);
  }

  @DeleteMapping("/{id}")
  public ApiResponse<String> delete(
      @PathVariable Long id,
      HttpServletRequest request) {
    service.delete(id);
    return ApiResponse.ok("Característica eliminada exitosamente", request);
  }

  @PostMapping("/bulk")
  public ApiResponse<List<FeatureResponse>> bulkGetOrCreate(
      @Valid @RequestBody List<FeatureRequest> requests,
      HttpServletRequest request) {
    return ApiResponse.ok(service.getOrCreateAll(requests), request);
  }
}