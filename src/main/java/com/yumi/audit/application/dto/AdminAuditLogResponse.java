package com.yumi.audit.application.dto;

import java.time.Instant;

public record AdminAuditLogResponse(
    String id,
    String adminEmail,
    String role,
    String action,
    Instant createdAt) {
}