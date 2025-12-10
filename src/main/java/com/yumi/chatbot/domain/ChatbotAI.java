package com.yumi.chatbot.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.*;

@Document("chatbot_sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotAI {

  @Id
  private String id;

  @Indexed(name = "session_id_idx")
  private String sessionId;

  private String ownerEmail; // Usuario autenticado o anonimo

  @Builder.Default
  private List<Context> messages = new ArrayList<>();

  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;

  public Context getOrCreateContext() {
    if (messages.isEmpty()) {
      Context ctx = Context.builder().chatId(this.id).build();
      messages.add(ctx);
      return ctx;
    }
    Context last = messages.get(messages.size() - 1);
    return last.isResolved() ? addNewContext() : last;
  }

  private Context addNewContext() {
    Context ctx = Context.builder().chatId(this.id).build();
    if (this.messages == null)
      this.messages = new ArrayList<>();
    this.messages.add(ctx);
    return ctx;
  }

  public void addContext(Context ctx) {
    if (this.messages == null)
      this.messages = new ArrayList<>();
    this.messages.add(ctx);
  }

  public ChatbotAI(String sessionId, String ownerEmail) {
    this.sessionId = sessionId;
    this.ownerEmail = ownerEmail;
    this.messages = new ArrayList<>();
  }
}
