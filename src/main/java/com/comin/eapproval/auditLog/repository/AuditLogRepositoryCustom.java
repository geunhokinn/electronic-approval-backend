package com.comin.eapproval.auditLog.repository;

import com.comin.eapproval.auditLog.dto.response.AuditLogResponse;
import com.comin.eapproval.auditLog.entity.enums.AuditLogAction;
import com.comin.eapproval.auditLog.entity.enums.AuditLogResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * QueryDSL을 이용한 감사 로그 조회 기능을 정의하는 Repository.
 */
public interface AuditLogRepositoryCustom {

    /**
     * 조건에 맞는 감사 로그 목록을 조회한다.
     *
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @param action 감사 행위
     * @param result 감사 결과
     * @param pageable 페이징 정보
     * @return 조건에 맞는 감사 로그 목록
     */
    Page<AuditLogResponse> searchAuditLogs(
            LocalDate startDate,
            LocalDate endDate,
            AuditLogAction action,
            AuditLogResult result,
            Pageable pageable
    );
}