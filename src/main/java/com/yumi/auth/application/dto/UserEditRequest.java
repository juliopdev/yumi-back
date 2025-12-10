package com.yumi.auth.application.dto;

import jakarta.validation.constraints.Email;

public record UserEditRequest(
    @Email String newEmail,
    String newFullName) {
}