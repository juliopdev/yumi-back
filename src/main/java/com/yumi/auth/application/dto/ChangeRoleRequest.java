package com.yumi.auth.application.dto;

import com.yumi.auth.domain.UserRole;
import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(@NotNull UserRole role) {
}