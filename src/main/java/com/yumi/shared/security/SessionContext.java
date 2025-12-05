package com.yumi.shared.security;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionContext {
  private String sessionId;

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String id) {
    this.sessionId = id;
  }
}