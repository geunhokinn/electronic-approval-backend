package com.comin.eapproval.dashboard.dto.response;

import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 최근 작성한 문서 정보를 제공하는 응답 객체.
 */
@Getter
public class RecentDocumentResponse {

    /**
     * 문서 ID.
     */
    private final Long documentId;

    /**
     * 문서 제목.
     */
    private final String title;

    /**
     * 문서 상태.
     */
    private final DocumentStatus status;

    /**
     * 결재자 이름.
     */
    private final String approverName;

    /**
     * 문서 작성일.
     */
    private final LocalDateTime createdAt;

    @QueryProjection
    public RecentDocumentResponse(
            Long documentId,
            String title,
            DocumentStatus status,
            String approverName,
            LocalDateTime createdAt
    ) {
        this.documentId = documentId;
        this.title = title;
        this.status = status;
        this.approverName = approverName;
        this.createdAt = createdAt;
    }
}