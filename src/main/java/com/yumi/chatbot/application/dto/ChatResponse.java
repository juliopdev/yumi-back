package com.yumi.chatbot.application.dto;

import com.yumi.chatbot.domain.Intent;

public record ChatResponse(
    Intent type,
    ChatContentResponse content,
    boolean isResolved) {
}