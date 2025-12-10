package com.yumi.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SessionContextFilter extends OncePerRequestFilter {

  private final SessionContext sessionContext;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
      filterChain.doFilter(request, response);
      return;
    }

    String sessionId = request.getHeader("X-Session-Id");
    if (sessionId != null && !sessionId.isBlank()) {
      sessionContext.setSessionId(sessionId);
    }
    filterChain.doFilter(request, response);
  }
}