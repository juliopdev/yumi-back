package com.yumi.chatbot.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("chatbot_daily_usage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyUsage {

  @Id
  private String id; // sessionId + "_" + date

  private String sessionId;
  private LocalDate date;
  private int count;
}