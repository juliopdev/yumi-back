package com.yumi.audit.application;

import com.yumi.audit.application.dto.AdminAuditLogResponse;
import com.yumi.audit.application.mapper.AdminAuditLogMapper;
import com.yumi.audit.infrastructure.persistence.MongoAdminAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * Casos de uso del log de auditoría de administradores.
 */
@Service
@RequiredArgsConstructor
public class AdminAuditLogService {

  private final MongoAdminAuditLogRepository repository;

  public Page<AdminAuditLogResponse> findAll(@NonNull Pageable pageable) {
    return repository.findAll(pageable)
        .map(AdminAuditLogMapper::toResponse);
  }
}