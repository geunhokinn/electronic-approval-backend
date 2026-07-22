package com.comin.eapproval.auditLog.service;

import com.comin.eapproval.auditLog.entity.AuditLog;
import com.comin.eapproval.auditLog.entity.enums.AuditLogAction;
import com.comin.eapproval.auditLog.entity.enums.AuditLogResult;
import com.comin.eapproval.auditLog.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 감사 로그를 저장하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class AuditLogWriter {

    private final AuditLogRepository auditLogRepository;

    /**
     * 감사 로그를 별도의 트랜잭션으로 저장합니다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void write(
            Long documentId,
            Long actorId,
            AuditLogAction action,
            AuditLogResult result,
            String reason
    ) {
        AuditLog auditLog = AuditLog.create(
                documentId,
                actorId,
                action,
                result,
                reason
        );

        auditLogRepository.save(auditLog);
    }
}