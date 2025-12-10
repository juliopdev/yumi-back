package com.yumi.chatbot.application;

import com.yumi.chatbot.application.dto.ChatContentResponse;
import com.yumi.chatbot.application.dto.ChatResponse;
import com.yumi.chatbot.domain.ChatbotAI;
import com.yumi.chatbot.domain.Context;
import com.yumi.chatbot.domain.Intent;
import com.yumi.chatbot.infrastructure.persistence.MongoChatbotAiRepository;
import com.yumi.chatbot.infrastructure.persistence.MongoContextRepository;
import com.yumi.catalog.application.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fabrica respuestas y gestiona ciclo de vida del contexto.
 */
@Component
@RequiredArgsConstructor
public class ChatResponseFactory {

  private final MongoChatbotAiRepository chatRepository;
  private final MongoContextRepository contextRepository;

  public ChatResponse build(ChatbotAI chat,
      String text,
      Intent intent,
      boolean resolved,
      List<ProductResponse> products,
      boolean addToCart) {

    Context ctx = chat.getOrCreateContext();
    ctx.addBotMessage(text, intent, products == null ? List.of()
        : products.stream()
            .map(ProductResponse::id)
            .toList());

    if (resolved) {
      Context newCtx = Context.builder()
          .chatId(chat.getId())
          .build();
      chat.addContext(newCtx);
      contextRepository.save(newCtx);
    }
    chatRepository.save(chat);

    return new ChatResponse(
        intent,
        new ChatContentResponse(text, products, addToCart, true),
        resolved);
  }

  public ChatResponse notAuthorized() {
    return new ChatResponse(
        Intent.NO_AUTHORIZED,
        new ChatContentResponse(
            "Debes iniciar sesión para esta acción.",
            null,
            false,
            false),
        false);
  }
}