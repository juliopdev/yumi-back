package com.yumi.audit.infrastructure.web;

import com.yumi.audit.application.AdminAuditLogService;
import com.yumi.audit.application.dto.AdminAuditLogResponse;
import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AdminAuditLogController.BASE_PATH)
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
public class AdminAuditLogController {

  public static final String BASE_PATH = "/api/admin/audit-logs";
  private final AdminAuditLogService service;

  @GetMapping
  public ApiResponse<Page<AdminAuditLogResponse>> list(
      @ParameterObject @PageableDefault(size = 20) Pageable pageable,
      HttpServletRequest request) {
    return ApiResponse.ok(service.findAll(pageable), request);
  }
}