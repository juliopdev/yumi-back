package com.yumi.auth.infrastructure.web;

import com.yumi.auth.application.ProfileService;
import com.yumi.auth.application.dto.UserEditRequest;
import com.yumi.auth.application.dto.UserResponse;
import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class UserController {

  private final ProfileService profileService;

  @GetMapping
  public ApiResponse<UserResponse> getProfile(HttpServletRequest request) {
    UserResponse body = profileService.getMyProfile();
    return ApiResponse.ok(body, request);
  }

  @PutMapping
  public ApiResponse<UserResponse> updateProfile(
      @Valid @RequestBody UserEditRequest req,
      HttpServletRequest request) {
    UserResponse body = profileService.updateProfile(req);
    return ApiResponse.ok(body, request);
  }
}