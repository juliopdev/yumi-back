package com.yumi.cart.application.dto;

import java.math.BigDecimal;

public record CartItemResponse(
    Long id,
    String productSku,
    String productName,
    BigDecimal unitPrice,
    String imageUrl,
    Integer quantity) {
}