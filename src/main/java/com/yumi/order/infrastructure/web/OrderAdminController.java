package com.yumi.order.infrastructure.web;

import com.yumi.order.application.OrderAdminService;
import com.yumi.order.application.dto.OrderResponse;
import com.yumi.order.application.dto.OrderStatusRequest;
import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SHIPPINGMANAGER')")
public class OrderAdminController {

  private final OrderAdminService service;

  @GetMapping
  public ApiResponse<Page<OrderResponse>> listAll(
      @PageableDefault(size = 20) Pageable pageable,
      HttpServletRequest request) {
    Page<OrderResponse> body = service.listAll(pageable);
    return ApiResponse.ok(body, request);
  }

  @PatchMapping("/{sku}/status")
  public ApiResponse<OrderResponse> changeStatus(
      @PathVariable String sku,
      @Valid @RequestBody OrderStatusRequest req,
      HttpServletRequest request) {
    OrderResponse body = service.changeStatus(sku, req);
    return ApiResponse.ok(body, request);
  }
}