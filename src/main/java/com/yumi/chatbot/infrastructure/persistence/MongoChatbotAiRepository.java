package com.yumi.chatbot.infrastructure.persistence;

import com.yumi.chatbot.domain.ChatbotAI;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface MongoChatbotAiRepository extends MongoRepository<ChatbotAI, String> {

  @Query(value = "{ 'sessionId' : ?0 }", fields = "{ 'messages' : 0 }")
  Optional<ChatbotAI> findBySessionId(String sessionId);

  @Query(value = "{ 'sessionId' : ?0, 'messages.resolved' : false }")
  Optional<ChatbotAI> findBySessionIdWithContextUnresolved(String sessionId);

  // Optional<ChatbotAI> findBySessionIdWithContextWithMessagesUnresolved(String
  // sessionId);
}