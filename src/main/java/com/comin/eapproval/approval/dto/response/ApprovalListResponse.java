package com.comin.eapproval.approval.dto.response;

import com.comin.eapproval.document.entity.enums.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 결재 목록 조회 응답 DTO
 */
@Getter
@AllArgsConstructor
public class ApprovalListResponse {

    private final Long approvalId;

    private final Long documentId;

    private final String title;

    private final String writerName;

    private final DocumentStatus status;

    private final LocalDateTime requestedAt;

    private final LocalDateTime processedAt;
}
