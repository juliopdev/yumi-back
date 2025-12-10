package com.yumi.chatbot.application.handlers;

import org.springframework.stereotype.Component;

import com.yumi.chatbot.application.ChatResponseFactory;
import com.yumi.chatbot.application.dto.ChatResponse;
import com.yumi.chatbot.domain.ChatbotAI;
import com.yumi.chatbot.domain.Intent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GreetingHandler {
  private final ChatResponseFactory responseFactory;

  public ChatResponse handle(ChatbotAI chat, String msg) {
    return responseFactory.build(chat,
        "¡Hola! Soy YumiBot, ¿qué producto te interesa?",
        Intent.GREETING,
        true,
        null,
        false);
  }
}