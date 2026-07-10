package com.comin.eapproval.auditLog.repository;

import com.comin.eapproval.auditLog.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
