package com.comin.eapproval.auditLog.dto.response;

import com.comin.eapproval.auditLog.entity.enums.AuditLogAction;
import com.comin.eapproval.auditLog.entity.enums.AuditLogResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 감사 로그 조회 결과를 반환하는 응답 객체.
 */
@Getter
@AllArgsConstructor
public class AuditLogResponse {

    private LocalDateTime createdAt;

    private String actorName;

    private String documentTitle;

    private AuditLogAction action;

    private AuditLogResult result;

    private String reason;
}