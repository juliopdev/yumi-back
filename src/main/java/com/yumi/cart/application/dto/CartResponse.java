package com.yumi.cart.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
    Long cartId,
    String ownerEmail,
    List<CartItemResponse> items,
    BigDecimal baseImponible,
    BigDecimal igv,
    BigDecimal igv_rate,
    BigDecimal totalConIGV) {
}