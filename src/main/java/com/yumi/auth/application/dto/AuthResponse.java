package com.yumi.auth.application.dto;

public record AuthResponse(String token, UserResponse user) {
}