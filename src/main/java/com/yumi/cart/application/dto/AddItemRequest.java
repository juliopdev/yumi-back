package com.yumi.cart.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddItemRequest(
    @NotNull String productSku,
    @NotNull @Min(1) Integer quantity) {
}