package com.yumi.chatbot.application;

import com.yumi.chatbot.domain.DailyUsage;
import com.yumi.chatbot.infrastructure.persistence.DailyUsageRepository;
import com.yumi.shared.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

/**
 * Rate-limit 15 s + límite diario 40 mensajes por sessionId.
 */
@Component
@RequiredArgsConstructor
public class RateLimiter {

  private final DailyUsageRepository dailyUsageRepository;

  public void check(String sessionId) {
    LocalDate today = LocalDate.now();
    String id = sessionId + "_" + today;

    DailyUsage usage = dailyUsageRepository.findById(id)
        .orElse(DailyUsage.builder()
            .id(id)
            .sessionId(sessionId)
            .date(today)
            .count(0)
            .build());

    if (usage.getCount() >= 40) {
      throw new BadRequestException("Has alcanzado el límite de 40 mensajes por hoy.");
    }

    usage.setCount(usage.getCount() + 1);
    dailyUsageRepository.save(usage);
  }
}