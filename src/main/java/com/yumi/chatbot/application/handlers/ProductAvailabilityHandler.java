package com.yumi.chatbot.application.handlers;

import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Component;

import com.yumi.catalog.application.ProductService;
import com.yumi.chatbot.application.ChatResponseFactory;
import com.yumi.chatbot.application.dto.ChatResponse;
import com.yumi.chatbot.domain.ChatbotAI;
import com.yumi.chatbot.domain.Intent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductAvailabilityHandler {
  private final ChatResponseFactory responseFactory;
  private final VertexAiGeminiChatModel gemini;
  private final ProductService productService; // ya existe

  public ChatResponse handle(ChatbotAI chat, String msg) {
    String prompt = "Extrae el producto de: \"" + msg + "\". Solo responde el nombre.";
    String product = gemini.call(prompt).trim();

    var products = productService.findByNameContaining(product);
    if (products.isEmpty()) {
      return responseFactory.build(chat,
          "Por ahora no tenemos " + product + ", pero estos productos pueden interesarte:",
          Intent.PRODUCT_AVAILABILITY,
          true,
          productService.randomVisible(3),
          false);
    }
    return responseFactory.build(chat,
        "Sí, tenemos estos " + product + ":",
        Intent.PRODUCT_AVAILABILITY,
        true,
        products,
        true);
  }
}