package com.comin.eapproval.auditLog.service;

import com.comin.eapproval.auditLog.dto.request.AuditLogSearchRequest;
import com.comin.eapproval.auditLog.dto.response.AuditLogResponse;
import com.comin.eapproval.auditLog.repository.AuditLogRepository;
import com.comin.eapproval.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 감사 로그 조회 기능을 제공하는 Service.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * 검색 조건에 맞는 감사 로그 목록을 조회한다.
     *
     * @param request 감사 로그 검색 조건
     * @param pageable 페이징 정보
     * @return 감사 로그 목록
     */
    public PageResponse<AuditLogResponse> searchAuditLogs(
            AuditLogSearchRequest request,
            Pageable pageable
    ) {
        Page<AuditLogResponse> auditLogs =
                auditLogRepository.searchAuditLogs(
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getAction(),
                        request.getResult(),
                        pageable
                );

        return PageResponse.from(auditLogs);
    }
}