package com.yumi.chatbot.application.handlers;

import com.yumi.catalog.application.ProductService;
import com.yumi.catalog.application.dto.ProductResponse;
import com.yumi.chatbot.application.ChatResponseFactory;
import com.yumi.chatbot.application.dto.ChatResponse;
import com.yumi.chatbot.domain.ChatbotAI;
import com.yumi.chatbot.domain.Intent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationHandler {

  private final ProductService productService;
  private final ChatResponseFactory factory;

  @Cacheable(value = "recommendations", key = "'default'")
  public ChatResponse handle(ChatbotAI chat, String msg) {
    List<ProductResponse> products = productService.randomVisible(3);
    return factory.build(chat,
        "Aquí tienes algunas sugerencias:",
        Intent.RECOMMENDATION,
        true,
        products,
        false);
  }
}