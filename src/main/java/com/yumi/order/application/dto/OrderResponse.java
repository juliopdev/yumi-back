package com.yumi.order.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import com.yumi.address.application.dto.AddressResponse;
import com.yumi.order.domain.OrderStatus;



public record OrderResponse(
    Long id,
    String orderSku,
    String customerEmail,
    OrderStatus status,
    AddressResponse addressDetail,
    List<OrderItemResponse> items,
    BigDecimal total,
    Instant createdAt) {
}