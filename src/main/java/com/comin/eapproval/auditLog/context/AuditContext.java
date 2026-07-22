package com.comin.eapproval.auditLog.context;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 감사 로그에 필요한 정보를 보관하는 객체입니다.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuditContext {

    private final Long actorId;

    private final Long documentId;

    public static AuditContext of(
            Long actorId,
            Long documentId
    ) {
        return new AuditContext(actorId, documentId);
    }
}
