package com.comin.eapproval.auditLog.controller;

import com.comin.eapproval.auditLog.dto.request.AuditLogSearchRequest;
import com.comin.eapproval.auditLog.dto.response.AuditLogResponse;
import com.comin.eapproval.auditLog.service.AuditLogService;
import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 감사 로그 조회 API를 제공하는 Controller.
 */
@RestController
@RequestMapping("/admin/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * 검색 조건에 맞는 감사 로그 목록을 조회한다.
     *
     * @param request 감사 로그 검색 조건
     * @param pageable 페이징 정보
     * @return 감사 로그 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> searchAuditLogs(
            @ModelAttribute AuditLogSearchRequest request,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        PageResponse<AuditLogResponse> auditLogs =
                auditLogService.searchAuditLogs(
                        request,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(auditLogs)
        );
    }
}