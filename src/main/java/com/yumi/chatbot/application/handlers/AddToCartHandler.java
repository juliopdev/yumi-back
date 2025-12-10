package com.yumi.chatbot.application.handlers;

import com.yumi.catalog.application.ProductService;
import com.yumi.catalog.application.dto.ProductResponse;
import com.yumi.chatbot.application.ChatResponseFactory;
import com.yumi.chatbot.application.dto.ChatResponse;
import com.yumi.chatbot.domain.ChatbotAI;
import com.yumi.chatbot.domain.Context;
import com.yumi.chatbot.domain.Intent;
import com.yumi.chatbot.domain.Message;
import com.yumi.shared.util.StringNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AddToCartHandler {

  private final ProductService productService;
  private final ChatResponseFactory factory;

  public ChatResponse handle(ChatbotAI chat, String msg) {
    Context ctx = chat.getOrCreateContext();
    Message last = ctx.getLastMessage();
    if (last != null && last.getType() == Intent.ADD_TO_CART_AWAIT_CONFIRM) {
      boolean confirmed = StringNormalizer.toSlug(msg).matches(".*(si|confirmar|claro|ok).*");
      if (!confirmed) {
        return factory.build(chat,
            "Perfecto, no se añadió nada al carrito.",
            Intent.ADD_TO_CART,
            true,
            null,
            false);
      }
      Long productId = last.getProducts().get(0);
      ProductResponse p = productService.getById(productId);
      return factory.build(chat,
          "Producto *" + p.name() + "* añadido al carrito.",
          Intent.ADD_TO_CART,
          true,
          List.of(p),
          true);
    }

    String prod = StringNormalizer.toSlug(msg)
        .replaceAll("agrega|añade|pon|mete|al.carrito", "")
        .trim();
    if (prod.isEmpty()) {
      return factory.build(chat,
          "¿Qué producto deseas añadir?",
          Intent.ADD_TO_CART,
          false,
          null,
          false);
    }
    List<ProductResponse> found = productService.findByNameContaining(prod);
    if (found.isEmpty()) {
      List<ProductResponse> related = productService.randomVisible(3);
      return factory.build(chat,
          "No tengo ese producto, pero quizás estos te sirvan.",
          Intent.ADD_TO_CART,
          false,
          related,
          false);
    }
    ProductResponse p = found.get(0);
    return factory.build(chat,
        "¿Confirmamos añadir 1 unidad de *" + p.name() + "* al carrito?",
        Intent.ADD_TO_CART_AWAIT_CONFIRM,
        false,
        List.of(p),
        true);
  }
}