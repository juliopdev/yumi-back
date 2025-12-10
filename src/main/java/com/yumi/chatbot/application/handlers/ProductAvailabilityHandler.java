package com.yumi.chatbot.application.handlers;

import java.util.List;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.yumi.catalog.application.ProductService;
import com.yumi.catalog.application.dto.ProductResponse;
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
  private final ProductService productService;

  public ChatResponse handle(ChatbotAI chat, String msg) {
    String prompt = "Extrae de la siguiente \"Frase\" el **producto** o **categoría** que el usuario desea obtener. " +
        "Responde solo UNA palabra o breve frase, en minúsculas, sin puntuación. " +
        "Ejemplos: \"smartwatch\", \"juguetes\", \"iphone 15\", \"hogar\", \"moda\", \"pistola hidrogel\", \"peluche\".\n\n" +
        "Frase: \"" + msg + "\"";
    String term = gemini.call(prompt).trim().toLowerCase();

    if (term.isEmpty() || term.equals("no especifica") || term.equals("none") || term.equals("general")) {
      return fallback(chat);
    }

    List<ProductResponse> byCategory = productService.getAllVisibleByCategoryName(term, Pageable.ofSize(5))
        .getContent();
    if (!byCategory.isEmpty()) {
      return responseFactory.build(chat,
          "Sí, estos son algunos productos de **" + term + "**:",
          Intent.PRODUCT_AVAILABILITY,
          true,
          byCategory,
          true);
    }

    List<ProductResponse> byName = productService.findByNameContaining(term.replaceAll("\\s+", " "));
    System.out.println("\nbyName: ");
    System.out.println(byName);
    if (!byName.isEmpty()) {
      return responseFactory.build(chat,
          "Sí, tenemos estos **" + term + "**:",
          Intent.PRODUCT_AVAILABILITY,
          true,
          byName,
          true);
    }

    return responseFactory.build(chat,
        "Por ahora no tenemos **" + term + "**, pero estos productos pueden interesarte:",
        Intent.PRODUCT_AVAILABILITY,
        true,
        productService.randomVisible(6),
        false);
  }

  private ChatResponse fallback(ChatbotAI chat) {
    return responseFactory.build(chat,
        "No encontré ese producto, pero estos pueden interesarte:",
        Intent.PRODUCT_AVAILABILITY,
        true,
        productService.randomVisible(6),
        false);
  }
}