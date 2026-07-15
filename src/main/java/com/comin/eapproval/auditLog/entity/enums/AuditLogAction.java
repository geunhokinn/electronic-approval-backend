package com.comin.eapproval.auditLog.entity.enums;

public enum AuditLogAction {

    // 문서
    DOCUMENT_CREATE,
    DOCUMENT_UPDATE,
    DOCUMENT_DELETE,

    // 결재
    APPROVAL_REQUEST,
    APPROVAL_APPROVE,
    APPROVAL_REJECT,
    APPROVAL_RESUBMIT
}
