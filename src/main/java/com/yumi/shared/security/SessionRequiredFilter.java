package com.yumi.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SessionRequiredFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String sessionId = request.getHeader("X-Session-Id");
    if (sessionId == null || sessionId.isBlank()) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setContentType("application/json");
      response.getWriter().write("{\"error\":\"Missing X-Session-Id header\"}");
      return;
    }
    filterChain.doFilter(request, response);
  }
}