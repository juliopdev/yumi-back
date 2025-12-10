package com.yumi.order.application.mapper;

import java.math.BigDecimal;

import com.yumi.catalog.domain.Product;
import com.yumi.order.application.dto.OrderItemRequest;
import com.yumi.order.application.dto.OrderItemResponse;
import com.yumi.order.domain.OrderItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Transformaciones entre OrderItem y OrderItemResponse.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderItemMapper {

  public static OrderItem toEntity(OrderItemRequest req, Product p) {
    return OrderItem.builder()
        .productSku(p.getSku())
        .productName(p.getName())
        .unitPrice(p.getPrice())
        .quantity(req.quantity())
        .subtotal(p.getPrice().multiply(BigDecimal.valueOf(req.quantity())))
        .build();
  }

  public static OrderItemResponse toResponse(OrderItem i) {
    return new OrderItemResponse(
        i.getId(),
        i.getProductSku(),
        i.getProductName(),
        i.getUnitPrice(),
        i.getQuantity(),
        i.getSubtotal());
  }
}