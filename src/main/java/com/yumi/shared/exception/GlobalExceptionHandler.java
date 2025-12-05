package com.yumi.shared.exception;

import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BadRequestException.class)
  public ApiResponse<Void> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
    return ApiResponse.error("BAD_REQUEST", ex.getMessage(), req, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ApiResponse<Void> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
    return ApiResponse.error("RESOURCE_NOT_FOUND", ex.getMessage(), req, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ApiResponse<Void> handleConflict(DuplicateResourceException ex, HttpServletRequest req) {
    return ApiResponse.error("DUPLICATE_RESOURCE", ex.getMessage(), req, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ApiResponse<Void> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
    return ApiResponse.error("INVALID_CREDENTIALS", ex.getMessage(), req, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ApiResponse<Void> handleIllegalState(IllegalStateException ex, HttpServletRequest req) {
    return ApiResponse.error("DEPENDENCY_VIOLATION", ex.getMessage(), req, HttpStatus.CONFLICT);
  }
}