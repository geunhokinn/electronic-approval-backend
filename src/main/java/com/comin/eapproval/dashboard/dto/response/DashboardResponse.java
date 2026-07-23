package com.comin.eapproval.dashboard.dto.response;

import lombok.Getter;

import java.util.List;

/**
 * 대시보드 화면에 필요한 정보를 제공하는 응답 객체.
 */
@Getter
public class DashboardResponse {

    /**
     * 내 문서 상태별 건수.
     */
    private final DocumentCountResponse documentCounts;

    /**
     * 최근 작성한 문서 목록.
     */
    private final List<RecentDocumentResponse> recentDocuments;

    /**
     * 나에게 배정된 결재 대기 문서 목록.
     */
    private final List<PendingApprovalResponse> pendingApprovals;

    public DashboardResponse(
            DocumentCountResponse documentCounts,
            List<RecentDocumentResponse> recentDocuments,
            List<PendingApprovalResponse> pendingApprovals
    ) {
        this.documentCounts = documentCounts;
        this.recentDocuments = recentDocuments;
        this.pendingApprovals = pendingApprovals;
    }
}