package com.yumi.chatbot.infrastructure.web;

import com.yumi.auth.domain.User;
import com.yumi.auth.infrastructure.security.AuthContext;
import com.yumi.chatbot.application.ChatbotService;
import com.yumi.chatbot.application.dto.ChatRequest;
import com.yumi.chatbot.application.dto.ChatResponse;
import com.yumi.chatbot.domain.ChatbotAI;
import com.yumi.chatbot.infrastructure.persistence.MongoChatbotAiRepository;
import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatbotController {

  private final ChatbotService service;
  private final MongoChatbotAiRepository chatRepository;
  private final AuthContext authContext;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<ChatResponse> chat(
      @Valid @RequestBody ChatRequest req,
      HttpServletRequest request) {
    ChatResponse body = service.reply(req);
    return ApiResponse.ok(body, request);
  }

  @PostMapping("/init")
  public ApiResponse<Map<String, String>> init(HttpServletRequest request) {
    String sessionId = UUID.randomUUID().toString();
    ChatbotAI chat = ChatbotAI.builder()
        .sessionId(sessionId)
        .ownerEmail(authContext.currentUser().map(User::getEmail).orElse("anonymous"))
        .build();
    chatRepository.save(chat);
    return ApiResponse.ok(Map.of("sessionId", sessionId), request);
  }
}