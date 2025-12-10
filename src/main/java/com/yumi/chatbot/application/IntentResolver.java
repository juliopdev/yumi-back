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

  private static final Map<Intent, String> KEYWORDS = new java.util.LinkedHashMap<>();

  static {
    KEYWORDS.put(Intent.GREETING, "hola buenas buenos dias tardes noches");
    KEYWORDS.put(Intent.PRODUCT_LIST,
        "qué productos que productos tenemos disponibles catálogo lista completa todos");
    KEYWORDS.put(Intent.RECOMMENDATION,
        "recomienda sugiere qué producto me recomiendas qué me sugieres qué elegir");
    KEYWORDS.put(Intent.PRODUCT_AVAILABILITY, "tienes hay disponible");
    KEYWORDS.put(Intent.PURCHASE_INTENT, "comprar quiero necesito busco");
    KEYWORDS.put(Intent.ORDER_STATUS, "pedido estado número");
    KEYWORDS.put(Intent.DELIVERY_TIME, "entrega tiempo cuando");
    KEYWORDS.put(Intent.FAQ, "política devolución garantía");
    KEYWORDS.put(Intent.ADD_TO_CART, "agrega añade carrito");
  }

  public Intent resolve(String text) {
    String norm = StringNormalizer.toSlug(text);
    for (var entry : KEYWORDS.entrySet()) {
      String[] kws = entry.getValue().split(" ");
      for (String kw : kws) {
        if (kw.length() < 5)
          continue;
        if (norm.contains(kw))
          return entry.getKey();
      }
    }
    for (var entry : KEYWORDS.entrySet()) {
      for (String kw : entry.getValue().split(" ")) {
        if (kw.length() >= 5)
          continue;
        if (norm.contains(kw))
          return entry.getKey();
      }
    }
    String prompt = """
        Clasifica la intención en UNA palabra: faq, delivery_time, order_status, recommendation, add_to_cart, unknown.
        Mensaje: "%s"
        """.formatted(text);
    try {
      String raw = gemini.call(prompt).trim().toUpperCase();
      double conf = extractConfidence(raw);
      if (conf < 0.5)
        return Intent.UNKNOWN;
      return Intent.valueOf(raw.replaceAll("\\s*\\(.*\\)", ""));
    } catch (Exception e) {
      return Intent.UNKNOWN;
    }
  }

  private double extractConfidence(String raw) {
    java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\((\\d\\.?\\d*)\\)");
    var m = p.matcher(raw);
    return m.find() ? Double.parseDouble(m.group(1)) : 1.0;
  }
}