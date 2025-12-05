package com.yumi.audit.infrastructure.persistence;

import com.yumi.audit.domain.AdminAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoAdminAuditLogRepository extends MongoRepository<AdminAuditLog, String> {
}