package com.yumi.chatbot.application.handlers;

import com.yumi.chatbot.application.ChatResponseFactory;
import com.yumi.chatbot.application.dto.ChatResponse;
import com.yumi.chatbot.core.FactValidator;
import com.yumi.chatbot.domain.ChatbotAI;
import com.yumi.chatbot.domain.Intent;
import com.yumi.order.application.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusHandler {

  private final OrderService orderService;
  private final FactValidator validator;
  private final ChatResponseFactory factory;

  public ChatResponse handle(ChatbotAI chat, String msg) {
    String on = validator.extractOrderNumber(msg);
    if (on == null || on.isBlank()) {
      return factory.build(chat,
          "Por favor indícame el número de pedido (8 caracteres).",
          Intent.ORDER_STATUS,
          false,
          null,
          false);
    }
    var order = orderService.getMyOrderToChat(on);
    if (order.isEmpty()) {
      return factory.build(chat,
          "No encontré ese número de pedido.",
          Intent.ORDER_STATUS,
          false,
          null,
          false);
    }
    String status = order.get().status().toString();
    return factory.build(chat,
        "Tu pedido " + on + " está " + status + ".",
        Intent.ORDER_STATUS,
        true,
        null,
        false);
  }
}