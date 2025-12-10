package com.yumi.shared.exception;

import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<String> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
    log.warn("Bad request: {}", ex.getMessage());
    return ApiResponse.error("BAD_REQUEST", ex.getMessage(), req, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiResponse<String> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
    log.warn("Resource not found: {}", ex.getMessage());
    return ApiResponse.error("RESOURCE_NOT_FOUND", ex.getMessage(), req, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApiResponse<String> handleConflict(DuplicateResourceException ex, HttpServletRequest req) {
    log.warn("Duplicate resource: {}", ex.getMessage());
    return ApiResponse.error("DUPLICATE_RESOURCE", ex.getMessage(), req, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiResponse<String> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
    log.warn("Invalid credentials: {}", ex.getMessage());
    return ApiResponse.error("INVALID_CREDENTIALS", ex.getMessage(), req, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApiResponse<String> handleIllegalState(IllegalStateException ex, HttpServletRequest req) {
    log.warn("Dependency violation: {}", ex.getMessage());
    return ApiResponse.error("DEPENDENCY_VIOLATION", ex.getMessage(), req, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<String> handleAllExceptions(Exception ex, HttpServletRequest req) {
    String hash = Integer.toHexString(ex.hashCode());
    log.error("Unhandled error [{}]", hash, ex);
    return ApiResponse.error("INTERNAL_ERROR", "Error interno (%s)".formatted(hash), req, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}