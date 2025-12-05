package com.yumi.order.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

import com.yumi.address.application.dto.AddressRequest;

public record CreateOrderRequest(
    @NotNull String customerEmail,
    @NotNull Boolean isAnonymous,
    @NotNull AddressRequest addressDetail,
    @Valid @NotEmpty List<OrderItemRequest> items) {
}