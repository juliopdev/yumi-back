package com.yumi.chatbot.application;

import com.yumi.chatbot.application.dto.ChatRequest;
import com.yumi.chatbot.application.dto.ChatResponse;
import com.yumi.chatbot.application.handlers.AddToCartHandler;
import com.yumi.chatbot.application.handlers.DeliveryHandler;
import com.yumi.chatbot.application.handlers.FaqHandler;
import com.yumi.chatbot.application.handlers.GreetingHandler;
import com.yumi.chatbot.application.handlers.OrderStatusHandler;
import com.yumi.chatbot.application.handlers.ProductAvailabilityHandler;
import com.yumi.chatbot.application.handlers.RecommendationHandler;
import com.yumi.chatbot.domain.ChatbotAI;
import com.yumi.chatbot.domain.Intent;
import com.yumi.chatbot.infrastructure.persistence.MongoChatbotAiRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatbotService {

  private final IntentResolver intentResolver;
  private final RateLimiter rateLimiter;
  private final ChatResponseFactory responseFactory;
  private final FaqHandler faqHandler;
  private final DeliveryHandler deliveryHandler;
  private final OrderStatusHandler orderStatusHandler;
  private final RecommendationHandler recommendationHandler;
  private final AddToCartHandler addToCartHandler;
  private final MongoChatbotAiRepository chatRepository;
  private final GreetingHandler greetingHandler;
  private final ProductAvailabilityHandler productAvailabilityHandler;

  public ChatResponse reply(ChatRequest req) {
    rateLimiter.check(req.sessionId());

    ChatbotAI chat = chatRepository
        .findBySessionId(req.sessionId())
        .orElseGet(() -> {
          ChatbotAI nuevo = ChatbotAI.builder()
              .sessionId(req.sessionId())
              .ownerEmail("anonymous")
              .build();
          return chatRepository.save(nuevo);
        });

    Intent intent = intentResolver.resolve(req.message());
    chat.getOrCreateContext().addUserMessage(req.message());

    try {
      return switch (intent) {
        case GREETING -> greetingHandler.handle(chat, req.message());
        case PRODUCT_AVAILABILITY -> productAvailabilityHandler.handle(chat, req.message());
        case PURCHASE_INTENT -> recommendationHandler.handle(chat, req.message());
        case FAQ -> faqHandler.handle(chat, req.message());
        case DELIVERY_TIME -> deliveryHandler.handle(chat, req.message());
        case ORDER_STATUS -> orderStatusHandler.handle(chat, req.message());
        case RECOMMENDATION -> recommendationHandler.handle(chat, req.message());
        case ADD_TO_CART -> addToCartHandler.handle(chat, req.message());
        default -> responseFactory.build(chat,
            "Lo siento, no entiendo tu pregunta.",
            Intent.UNKNOWN,
            true,
            null,
            false);
      };
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException("Failed to generate content", ex);
    }
  }
}