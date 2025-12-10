package com.yumi.chatbot.application.handlers;

import com.yumi.about.application.AboutService;
import com.yumi.chatbot.application.ChatResponseFactory;
import com.yumi.chatbot.application.dto.ChatResponse;
import com.yumi.chatbot.domain.ChatbotAI;
import com.yumi.chatbot.domain.Intent;
import com.yumi.shared.util.StringNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FaqHandler {

  private final AboutService aboutService;
  private final ChatResponseFactory factory;

  public ChatResponse handle(ChatbotAI chat, String msg) {
    var faq = aboutService.getFaqs().stream()
        .filter(f -> StringNormalizer.toSlug(msg)
            .contains(StringNormalizer.toSlug(f.question())))
        .findFirst()
        .orElse(null);
    if (faq != null) {
      return factory.build(chat, faq.answer(), Intent.FAQ, true, null, false);
    }
    return factory.build(chat,
        "No tengo una respuesta para eso.",
        Intent.UNKNOWN,
        true,
        null,
        false);
  }
}