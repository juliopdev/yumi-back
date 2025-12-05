package com.yumi.chatbot.core;

import org.bson.Document;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.yumi.chatbot.domain.Context;
import com.yumi.chatbot.domain.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContextCascadeListener {

  private final MongoTemplate mongoTemplate;

  @EventListener
  public void handleBeforeDelete(BeforeDeleteEvent<Context> event) {
    log.warn("🔥 BeforeDeleteEvent<Context> disparado: {}", event.getDocument());
    Document document = event.getDocument();
    if (document != null) {
      String contextId = document.getString("_id");
      if (contextId != null) {
        mongoTemplate.remove(
            Query.query(Criteria.where("contextId").is(contextId)),
            Message.class);
      }
    }
  }
}