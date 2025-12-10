package com.yumi.chatbot.application.handlers;

import com.yumi.catalog.application.ProductService;
import com.yumi.catalog.application.dto.ProductResponse;
import com.yumi.chatbot.application.ChatResponseFactory;
import com.yumi.chatbot.application.dto.ChatResponse;
import com.yumi.chatbot.domain.ChatbotAI;
import com.yumi.chatbot.domain.Intent;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductListHandler {

  private final ProductService productService;
  private final ChatResponseFactory factory;

  public ChatResponse handle(ChatbotAI chat, String msg) {
    List<ProductResponse> products = productService.getAll(Pageable.ofSize(10)).getContent();
    if (products.isEmpty()) {
      return factory.build(chat,
          "Por ahora no tenemos productos en stock.",
          Intent.PRODUCT_LIST,
          true,
          null,
          false);
    }
    return factory.build(chat,
        "Estos son algunos de nuestros productos disponibles:",
        Intent.PRODUCT_LIST,
        true,
        products,
        false);
  }
}