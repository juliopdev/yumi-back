package com.yumi.order.infrastructure.web;

import com.yumi.order.application.OrderService;
import com.yumi.order.application.dto.CreateOrderRequest;
import com.yumi.order.application.dto.OrderResponse;
import com.yumi.shared.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class OrderController {

  private final OrderService service;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<OrderResponse> create(
      @Valid @RequestBody CreateOrderRequest req,
      HttpServletRequest request) {
    OrderResponse body = service.createOrder(req);
    return ApiResponse.ok(body, request);
  }

  @GetMapping
  public ApiResponse<Page<OrderResponse>> listMyOrders(
      @PageableDefault(size = 10) Pageable pageable,
      HttpServletRequest request) {
    Page<OrderResponse> body = service.myOrders(pageable);
    return ApiResponse.ok(body, request);
  }

  @GetMapping("/{sku}")
  public ApiResponse<OrderResponse> getMyOrder(
      @PathVariable String sku,
      HttpServletRequest request) {
    OrderResponse body = service.getMyOrder(sku);
    return ApiResponse.ok(body, request);
  }
}