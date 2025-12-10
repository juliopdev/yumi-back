package com.yumi.order.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
    @NotNull String productSku,
    @Min(1) Integer quantity) {
}