package com.yumi.order.application.dto;

public record UserInOrderResponse(
    Long id,
    String email,
    String fullName) {
}