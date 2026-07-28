package com.comin.eapproval.dashboard.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 결재 대기 문서 정보를 제공하는 응답 객체.
 */
@Getter
public class PendingApprovalResponse {

    /**
     * 결재 ID.
     */
    private final Long approvalId;

    /**
     * 문서 ID.
     */
    private final Long documentId;

    /**
     * 문서 제목.
     */
    private final String title;

    /**
     * 기안자 이름.
     */
    private final String writerName;

    /**
     * 결재 요청일.
     */
    private final LocalDateTime requestedAt;

    @QueryProjection
    public PendingApprovalResponse(
            Long approvalId,
            Long documentId,
            String title,
            String writerName,
            LocalDateTime requestedAt
    ) {
        this.approvalId = approvalId;
        this.documentId = documentId;
        this.title = title;
        this.writerName = writerName;
        this.requestedAt = requestedAt;
    }
}