package com.yumi.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest req,
      @NonNull HttpServletResponse res,
      @NonNull FilterChain chain) throws ServletException, IOException {

    String header = req.getHeader("Authorization");

    String email;
    String sessionId = null;
    String rolesCsv;

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      if (!jwtService.isTokenValid(token)) {
        chain.doFilter(req, res);
        return;
      }
      email = jwtService.extractUsername(token);
      sessionId = (String) jwtService.getClaims(token).get("sessionId");
      rolesCsv = (String) jwtService.getClaims(token).get("roles");
    } else {
      sessionId = req.getHeader("X-Session-Id");
      if (sessionId == null || sessionId.isBlank()) {
        sessionId = java.util.UUID.randomUUID().toString();
      }
      email = "anonymous";
      rolesCsv = null;
    }

    var authorities = (rolesCsv == null || rolesCsv.isBlank())
        ? java.util.List.<SimpleGrantedAuthority>of()
        : Arrays.stream(rolesCsv.split(","))
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
            .collect(Collectors.toList());

    var authToken = new UsernamePasswordAuthenticationToken(
        email, sessionId, authorities);
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
    SecurityContextHolder.getContext().setAuthentication(authToken);

    chain.doFilter(req, res);
  }
}