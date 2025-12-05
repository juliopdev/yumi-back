package com.yumi.order.application.dto;

import jakarta.validation.constraints.NotNull;
import com.yumi.order.domain.OrderStatus;

public record OrderStatusRequest(
    @NotNull OrderStatus status) {
}