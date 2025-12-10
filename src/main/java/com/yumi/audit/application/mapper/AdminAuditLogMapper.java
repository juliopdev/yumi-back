package com.yumi.audit.application.mapper;

import java.time.Instant;
import com.yumi.audit.application.dto.AdminAuditLogResponse;
import com.yumi.audit.domain.AdminAuditLog;
import com.yumi.shared.util.AesGcmUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Transformaciones entre modelo y DTO.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AdminAuditLogMapper {

  public static AdminAuditLog toEntity(String adminEmail, String role, String action) {
    return AdminAuditLog.builder()
        .adminEmail(adminEmail)
        .role(role)
        .action(action)
        .createdAt(Instant.now())
        .build();
  }

  public static AdminAuditLogResponse toResponse(AdminAuditLog log) {
    String plain = log.getActionEnc() != null
        ? AesGcmUtil.decrypt(log.getActionEnc())
        : "[SIN ACCION]";
    return new AdminAuditLogResponse(
        log.getId(),
        log.getAdminEmail(),
        log.getRole(),
        plain,
        log.getCreatedAt());
  }
}