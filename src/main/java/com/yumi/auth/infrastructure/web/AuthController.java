package com.yumi.auth.infrastructure.web;

import com.yumi.auth.application.AuthService;
import com.yumi.auth.application.dto.*;
import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AuthController.BASE_PATH)
@RequiredArgsConstructor
public class AuthController {

  public static final String BASE_PATH = "/api/auth";
  private final AuthService service;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<AuthResponse> register(
      @Valid @RequestBody RegisterRequest req,
      HttpServletRequest request) {
    return ApiResponse.ok(service.register(req), request);
  }

  @PostMapping("/login")
  public ApiResponse<AuthResponse> login(
      @Valid @RequestBody LoginRequest req,
      HttpServletRequest request) {
    return ApiResponse.ok(service.login(req), request);
  }

  @PutMapping("/password")
  public ApiResponse<AuthResponse> changePassword(
      @Valid @RequestBody ChangePasswordRequest req,
      HttpServletRequest request) {
    return ApiResponse.ok(service.changePassword(req), request);
  }
}