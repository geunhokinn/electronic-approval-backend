package com.comin.eapproval.document.dto.response;

import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 내 문서 목록 조회 결과를 제공하는 응답 객체.
 */
@Getter
public class DocumentListResponse {

    private final Long documentId;
    private final String title;
    private final DocumentStatus status;
    private final Long approverId;
    private final String approverName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @QueryProjection
    public DocumentListResponse(
            Long documentId,
            String title,
            DocumentStatus status,
            Long approverId,
            String approverName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.documentId = documentId;
        this.title = title;
        this.status = status;
        this.approverId = approverId;
        this.approverName = approverName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
