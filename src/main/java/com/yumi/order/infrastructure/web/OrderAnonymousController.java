package com.yumi.order.infrastructure.web;

import com.yumi.order.application.OrderService;
import com.yumi.order.application.dto.CreateOrderRequest;
import com.yumi.order.application.dto.OrderResponse;
import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/anonymous")
@RequiredArgsConstructor
public class OrderAnonymousController {
  private final OrderService orderService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<OrderResponse> createAnonymousOrder(
      @Valid @RequestBody CreateOrderRequest req,
      HttpServletRequest request) {
    String sessionId = request.getHeader("X-Session-Id");
    OrderResponse body = orderService.createAnonymousOrder(sessionId, req);
    return ApiResponse.ok(body, request);
  }
}