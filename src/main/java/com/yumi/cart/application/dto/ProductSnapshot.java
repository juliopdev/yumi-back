package com.yumi.cart.application.dto;

import java.math.BigDecimal;

public record ProductSnapshot(String sku,
    String name,
    String imageUrl,
    BigDecimal unitPrice) {
}