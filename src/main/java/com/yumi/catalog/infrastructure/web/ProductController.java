package com.yumi.catalog.infrastructure.web;

import com.yumi.catalog.application.ProductService;
import com.yumi.catalog.application.dto.ProductResponse;
import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService service;

  @GetMapping
  public ApiResponse<Page<ProductResponse>> list(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String feature,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @PageableDefault(size = 12) Pageable pageable,
      HttpServletRequest request) {
    System.out.println("🔎 Texto recibido: '" + search + "'");
    Page<ProductResponse> body = service.getAllVisibleWithFilters(category, search, minPrice, maxPrice, pageable);

    return ApiResponse.ok(body, request);
  }

  @GetMapping("/{id}")
  public ApiResponse<ProductResponse> getById(
      @PathVariable Long id,
      HttpServletRequest request) {
    return ApiResponse.ok(service.getById(id), request);
  }
}