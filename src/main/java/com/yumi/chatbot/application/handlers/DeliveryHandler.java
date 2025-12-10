package com.yumi.chatbot.application.handlers;

import com.yumi.auth.domain.User;
import com.yumi.auth.infrastructure.security.AuthContext;
import com.yumi.chatbot.application.ChatResponseFactory;
import com.yumi.chatbot.application.dto.ChatResponse;
import com.yumi.chatbot.core.DeliveryEstimator;
import com.yumi.chatbot.core.FactValidator;
import com.yumi.chatbot.domain.ChatbotAI;
import com.yumi.chatbot.domain.Intent;
import com.yumi.order.application.OrderService;
import com.yumi.order.domain.OrderStatus;
import com.yumi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryHandler {

  private final AuthContext authContext;
  private final OrderService orderService;
  private final DeliveryEstimator deliveryEstimator;
  private final FactValidator validator;
  private final ChatResponseFactory factory;

  public ChatResponse handle(ChatbotAI chat, String msg) {
    User me = authContext.currentUser()
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    String orderNumber = validator.extractOrderNumber(msg);
    if (orderNumber == null || orderNumber.isBlank()) {
      return factory.build(chat,
          "Por favor indícame el número de pedido (8 caracteres).",
          Intent.ORDER_STATUS,
          false,
          null,
          false);
    }
    var order = orderService.getMyOrder(orderNumber);
    if (!order.customerEmail().equals(me.getEmail())) {
      return factory.build(chat,
          "No encontré el pedido con ese número.",
          Intent.ORDER_STATUS,
          false,
          null,
          false);
    }
    if (order.status() != OrderStatus.SHIPPED) {
      return factory.build(chat,
          "Su pedido aún no ha sido enviado.",
          Intent.ORDER_STATUS,
          false,
          null,
          false);
    }
    String range = deliveryEstimator.estimate(
        order.addressDetail().city(),
        order.addressDetail().zipCode());
    return factory.build(chat,
        "El tiempo de entrega es de " + range + ".",
        Intent.DELIVERY_TIME,
        true,
        null,
        false);
  }
}