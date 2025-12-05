package com.yumi.chatbot.application;

import com.yumi.chatbot.domain.Intent;
import com.yumi.shared.util.StringNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Decide la intención del usuario: primero heurístico, luego Gemini.
 */
@Component
@RequiredArgsConstructor
public class IntentResolver {

  private final VertexAiGeminiChatModel gemini;

  private static final Map<Intent, String> KEYWORDS = Map.of(
      Intent.GREETING, "hola buenas buenos dias tardes noches",
      Intent.PRODUCT_AVAILABILITY, "tienes hay disponible",
      Intent.PURCHASE_INTENT, "comprar quiero necesito busco",
      Intent.ORDER_STATUS, "pedido estado número",
      Intent.DELIVERY_TIME, "entrega tiempo cuando",
      Intent.FAQ, "política devolución garantía",
      Intent.RECOMMENDATION, "recomienda sugiere",
      Intent.ADD_TO_CART, "agrega añade carrito");

  public Intent resolve(String text) {
    String norm = StringNormalizer.toSlug(text);
    // heurístico
    for (var entry : KEYWORDS.entrySet()) {
      for (String kw : entry.getValue().split(" ")) {
        if (norm.contains(kw))
          return entry.getKey();
      }
    }
    // fallback Gemini
    String prompt = """
        Clasifica la intención en UNA palabra: faq, delivery_time, order_status, recommendation, add_to_cart, unknown.
        Mensaje: "%s"
        """.formatted(text);
    try {
      String raw = gemini.call(prompt);
      System.out.println("📡 Gemini raw answer: " + raw);
      return Intent.valueOf(raw.trim().toUpperCase());
    } catch (Exception e) {
      e.printStackTrace();
      return Intent.UNKNOWN;
    }
  }
}