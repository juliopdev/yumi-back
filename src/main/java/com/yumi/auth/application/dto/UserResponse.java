package com.yumi.auth.application.dto;

import com.yumi.auth.domain.UserRole;

public record UserResponse(
    Long id,
    String email,
    String fullName,
    UserRole role) {
}