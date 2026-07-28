package com.comin.eapproval.approval.dto.response;

import com.comin.eapproval.document.entity.enums.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 결재 상세 조회 응답 DTO.
 */
@Getter
@AllArgsConstructor
public class ApprovalDetailResponse {

    private final Long approvalId;

    private final Long documentId;

    private final String title;

    private final String content;

    private final DocumentStatus status;

    private final Long writerId;

    private final String writerName;

    private final Long approverId;

    private final String approverName;

    private final LocalDateTime requestedAt;

    private final LocalDateTime processedAt;

    private final String comment;

    private final Long previousDocumentId;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;
}
