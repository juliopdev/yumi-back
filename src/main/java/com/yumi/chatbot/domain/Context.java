package com.yumi.chatbot.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Document("chatbot_context")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Context {

  @Id
  private String id;

  private String chatId;

  private String context; // Contexto resumido de todos los mensajes agregados

  @Builder.Default
  private List<Message> messages = new ArrayList<>();

  private int messageCount;

  private boolean resolved;

  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;

  @Indexed(name = "expire_at_idx", expireAfter = "2d")
  private Instant expireAt;

  public void setChatId(String chatId) {
    this.chatId = chatId;
  }

  public Message getLastMessage() {
    Message lastMessage = messages.get(messages.size() - 1);
    lastMessage.getMessageDecrypt();
    return lastMessage;
  }

  public void addUserMessage(String text) {
    if (this.messages == null) {
      this.messages = new ArrayList<>();
    }
    Message m = new Message();
    m.createMessage(text);
    this.messages.add(m);
    this.messageCount++;
  }

  public void addBotMessage(String text, Intent type, List<Long> products) {
    if (this.messages == null) {
      this.messages = new ArrayList<>();
    }
    Message m = new Message();
    m.createMessage(text, type, products);
    this.messages.add(m);
    this.messageCount++;
    if (type != null && type.name().endsWith("AWAIT_CONFIRM"))
      this.resolved = false;
  }

  protected void setUnresolved() {
    this.resolved = false;
    this.expireAt = null;
  }

  protected void setResolved() {
    if (resolved)
      return;
    this.expireAt = Instant.now().plus(2, ChronoUnit.DAYS);
    this.resolved = true;
  }

  public void setMessages(List<Message> msgs) {
    this.messages = new ArrayList<>(msgs);
  }
}