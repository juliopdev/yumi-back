package com.yumi.chatbot.infrastructure.persistence;

import com.yumi.chatbot.domain.DailyUsage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyUsageRepository extends MongoRepository<DailyUsage, String> {
  Optional<DailyUsage> findBySessionIdAndDate(String sessionId, LocalDate date);
}