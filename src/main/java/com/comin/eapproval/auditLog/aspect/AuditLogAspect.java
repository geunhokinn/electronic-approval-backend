package com.comin.eapproval.auditLog.aspect;

import com.comin.eapproval.auditLog.annotation.Audit;
import com.comin.eapproval.auditLog.context.AuditContext;
import com.comin.eapproval.auditLog.context.AuditContextHolder;
import com.comin.eapproval.auditLog.entity.enums.AuditLogResult;
import com.comin.eapproval.auditLog.service.AuditLogWriter;
import com.comin.eapproval.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 결재 작업의 성공 및 실패를 감사 로그로 기록합니다.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogWriter auditLogWriter;

    /**
     * 감사 로그 대상 메서드의 실행 결과를 기록합니다.
     */
    @Around("@annotation(audit)")
    public Object recordAuditLog(
            ProceedingJoinPoint joinPoint,
            Audit audit
    ) throws Throwable {

        try {
            Object result = joinPoint.proceed();

            saveSuccessLog(audit);

            return result;
        } catch (Throwable throwable) {
            saveFailLog(audit, throwable);

            throw throwable;
        } finally {
            AuditContextHolder.clear();
        }
    }

    /**
     * 성공 감사 로그를 저장합니다.
     */
    private void saveSuccessLog(Audit audit) {
        AuditContext context = AuditContextHolder.get();

        if (context == null) {
            return;
        }

        auditLogWriter.write(
                context.getDocumentId(),
                context.getActorId(),
                audit.action(),
                AuditLogResult.SUCCESS,
                null
        );
    }

    /**
     * 실패 감사 로그를 저장합니다.
     */
    private void saveFailLog(
            Audit audit,
            Throwable throwable
    ) {
        AuditContext context = AuditContextHolder.get();

        if (context == null) {
            return;
        }

        auditLogWriter.write(
                context.getDocumentId(),
                context.getActorId(),
                audit.action(),
                AuditLogResult.FAIL,
                resolveReason(throwable)
        );
    }

    /**
     * 예외에서 감사 로그에 저장할 실패 사유를 추출합니다.
     */
    private String resolveReason(Throwable throwable) {

        if (throwable instanceof BusinessException exception) {
            return exception.getErrorCode().getMessage();
        }

        String message = throwable.getMessage();

        if (message == null || message.isBlank()) {
            return "알 수 없는 오류가 발생했습니다.";
        }

        return message;
    }
}