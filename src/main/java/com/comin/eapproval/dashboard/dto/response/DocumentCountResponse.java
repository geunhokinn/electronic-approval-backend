package com.comin.eapproval.dashboard.dto.response;

import lombok.Getter;

/**
 * 내 문서 상태별 건수를 제공하는 응답 객체.
 */
@Getter
public class DocumentCountResponse {

    /**
     * 임시 저장 문서 수.
     */
    private final long draft;

    /**
     * 결재 대기 문서 수.
     */
    private final long pending;

    /**
     * 승인 완료 문서 수.
     */
    private final long approved;

    /**
     * 반려 문서 수.
     */
    private final long rejected;

    public DocumentCountResponse(
            long draft,
            long pending,
            long approved,
            long rejected
    ) {
        this.draft = draft;
        this.pending = pending;
        this.approved = approved;
        this.rejected = rejected;
    }
}