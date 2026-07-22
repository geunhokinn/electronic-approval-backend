package com.comin.eapproval.auditLog.annotation;

import com.comin.eapproval.auditLog.entity.enums.AuditLogAction;

import java.lang.annotation.*;

/**
 * 결재 작업에 대한 감사 로그 기록 대상을 지정한다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audit {

    AuditLogAction action();
}