package com.yumi.audit.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "admin_audit_logs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAuditLog {
  @Id
  private String id;
  private String adminEmail;
  private String role; // ADMIN | INVENTORY_MANAGER | SHIPPING_MANAGER
  private String action; // method + path
  @CreatedDate
  private Instant createdAt;
}