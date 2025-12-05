package com.yumi.chatbot.domain;

import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;

import com.yumi.shared.util.AesGcmUtil;

import org.springframework.data.annotation.Transient;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

  private String author;

  @Transient
  private String message;

  @Field("message_enc")
  private String messageEnc;

  private Intent type;

  private List<Long> products = new ArrayList<>();

  @CreatedDate
  private Instant createdAt;

  /* helpers */
  protected String getMessageDecrypt() {
    if (message == null && messageEnc != null) {
      this.message = AesGcmUtil.decrypt(messageEnc);
    }
    return message;
  }

  protected void createMessage(String message) {
    this.author = "User";
    this.message = message;
    this.messageEnc = AesGcmUtil.encrypt(message);
  }

  protected void createMessage(String message, Intent type, List<Long> products) {
    this.author = "YumiBot";
    this.message = message;
    this.type = type;
    this.products = products;
  }
}