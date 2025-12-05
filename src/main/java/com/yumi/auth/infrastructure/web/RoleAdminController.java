package com.yumi.auth.infrastructure.web;

import com.yumi.auth.application.RoleAdminService;
import com.yumi.auth.application.dto.ChangeRoleRequest;
import com.yumi.auth.application.dto.UserResponse;
import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class RoleAdminController {

  private final RoleAdminService roleAdminService;

  @GetMapping
  @Secured("ROLE_ADMIN")
  public ApiResponse<Page<UserResponse>> list(
      HttpServletRequest request,
      @PageableDefault(size = 12) Pageable pageable) {
    Page<UserResponse> body = roleAdminService.listAll(pageable);
    return ApiResponse.ok(body, request);
  }

  @PutMapping("/{userId}/role")
  @Secured("ROLE_ADMIN")
  public ApiResponse<UserResponse> changeRole(@PathVariable Long userId,
      @Valid @RequestBody ChangeRoleRequest req,
      HttpServletRequest request) {
    UserResponse body = roleAdminService.changeRole(userId, req);
    return ApiResponse.ok(body, request);
  }
}