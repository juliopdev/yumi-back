package com.yumi.chatbot.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "chatbot.cleanup.enabled", havingValue = "true")
public class ChatbotCleanupScheduler {

  private final MongoChatbotAiRepository repository;
  public final Map<String, AtomicInteger> dailyCount = new ConcurrentHashMap<>();

  @Scheduled(cron = "${chatbot.cleanup.cron}")
  public void purgeOldSessions() {
    Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);
    long deleted = repository.findAll().stream()
        .filter(s -> s.getCreatedAt().isBefore(cutoff))
        .peek(s -> repository.deleteById(s.getId()))
        .count();
    log.info("Chatbot cleanup finished – {} sessions removed", deleted);
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void resetDailyCounters() {
    dailyCount.clear();
    log.info("Chatbot daily counters reset");
  }
}