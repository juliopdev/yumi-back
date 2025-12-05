package com.yumi.chatbot.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
    @NotBlank String message,
    String sessionId
) {
}