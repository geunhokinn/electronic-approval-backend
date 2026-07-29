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
//    private final DocumentCountResponse documentCounts;

    /**
     * 중요 공지 우선 최근 공지사항 목록.
     */
    private final List<DashboardNoticeResponse> notices;

    /**
     * 최근 작성한 문서 목록.
     */
    private final List<RecentDocumentResponse> recentDocuments;

    /**
     * 나에게 배정된 결재 대기 문서 목록.
     */
//    private final List<PendingApprovalResponse> pendingApprovals;

    /**
     * 대시보드 응답을 생성한다.
     *
     * @param notices 최근 공지사항 목록
     * @param recentDocuments 최근 작성 문서 목록
     */
    public DashboardResponse(
//            DocumentCountResponse documentCounts,
            List<DashboardNoticeResponse> notices,
            List<RecentDocumentResponse> recentDocuments
//            List<PendingApprovalResponse> pendingApprovals
    ) {
//        this.documentCounts = documentCounts;
        this.notices = notices;
        this.recentDocuments = recentDocuments;
//        this.pendingApprovals = pendingApprovals;
    }
}
