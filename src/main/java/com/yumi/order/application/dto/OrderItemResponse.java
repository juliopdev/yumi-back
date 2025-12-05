package com.yumi.order.application.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long id,
    String productSku,
    String productName,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal subtotal) {
}