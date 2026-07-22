package com.comin.eapproval.auditLog.context;

/**
 * 현재 요청의 감사 로그 정보를 ThreadLocal에 저장합니다.
 */
public final class AuditContextHolder {

    private static final ThreadLocal<AuditContext> CONTEXT = new ThreadLocal<>();

    private AuditContextHolder() {
    }

    /**
     * 감사 로그 정보를 저장합니다.
     */
    public static void set(AuditContext context) {
        CONTEXT.set(context);
    }

    /**
     * 감사 로그 정보를 조회합니다.
     */
    public static AuditContext get() {
        return CONTEXT.get();
    }

    /**
     * 감사 로그 정보를 제거합니다.
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
