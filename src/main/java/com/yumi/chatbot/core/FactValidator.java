package com.yumi.chatbot.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class FactValidator {

  private static final Pattern ORDER_PATTERN = Pattern.compile("\\b[A-Z0-9]{8}\\b");

  public String extractOrderNumber(String text) {
    var m = ORDER_PATTERN.matcher(text.toUpperCase());
    return m.find() ? m.group() : null;
  }

  public boolean looksLikeProductName(String text) {
    return text.length() > 2 && text.length() < 50;
  }
}