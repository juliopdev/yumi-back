package com.yumi.catalog.infrastructure.web;

import com.yumi.catalog.application.ProductService;
import com.yumi.catalog.application.dto.CreateProductRequest;
import com.yumi.catalog.application.dto.EditProductRequest;
import com.yumi.catalog.application.dto.ProductResponse;
import com.yumi.shared.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','INVENTORYMANAGER')")
public class ProductAdminController {

  private final ProductService service;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<ProductResponse> create(
      @Valid @ModelAttribute CreateProductRequest req,
      HttpServletRequest request) {
    return ApiResponse.ok(service.create(req), request);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<ProductResponse> update(
      @PathVariable Long id,
      @Valid @ModelAttribute EditProductRequest req,
      HttpServletRequest request) {
    return ApiResponse.ok(service.update(id, req), request);
  }

  @DeleteMapping("/{id}")
  public ApiResponse<String> delete(
      @PathVariable Long id,
      HttpServletRequest request) {
    service.delete(id);
    return ApiResponse.ok("Producto eliminado exitosamente", request);
  }

  @PatchMapping("/{id}/restore")
  public ApiResponse<ProductResponse> restore(
      @PathVariable Long id,
      HttpServletRequest request) {
    service.restore(id);
    return ApiResponse.ok(service.getById(id), request);
  }
}