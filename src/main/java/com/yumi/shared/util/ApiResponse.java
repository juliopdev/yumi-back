package com.yumi.shared.util;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;

public record ApiResponse<T>(
    boolean success,
    String path,
    T data,
    Error error,
    String timestamp) {

  /* 200-OK */
  public static <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
    return new ApiResponse<>(
        true,
        req.getRequestURI(),
        data,
        null,
        Instant.now().toString());
  }

  /* 4xx/5xx */
  public static <T> ApiResponse<T> error(
      String code, String message, HttpServletRequest req, HttpStatus status) {
    return new ApiResponse<>(
        false,
        req.getRequestURI(),
        null,
        new Error(code, message, status.value()),
        Instant.now().toString());
  }

  public record Error(String code, String message, int status) {
  }
}