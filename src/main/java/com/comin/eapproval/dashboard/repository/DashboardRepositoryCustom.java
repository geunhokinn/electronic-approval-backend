package com.comin.eapproval.dashboard.repository;

import com.comin.eapproval.dashboard.dto.response.DocumentCountResponse;
import com.comin.eapproval.dashboard.dto.response.DashboardNoticeResponse;
import com.comin.eapproval.dashboard.dto.response.PendingApprovalResponse;
import com.comin.eapproval.dashboard.dto.response.RecentDocumentResponse;

import java.util.List;

/**
 * 대시보드 화면에 필요한 사용자 정의 조회 기능을 제공하는 Repository.
 */
public interface DashboardRepositoryCustom {

    /**
     * 중요 공지를 우선하여 최근 공지사항을 조회한다.
     *
     * @param limit 조회할 최대 공지사항 수
     * @return 최근 공지사항 목록
     */
    List<DashboardNoticeResponse> findRecentNotices(long limit);

    /**
     * 작성한 문서의 상태별 건수를 조회한다.
     *
     * @param writerId 작성자 회원 ID
     * @return 문서 상태별 건수
     */
    DocumentCountResponse countDocumentsByStatus(Long writerId);

    /**
     * 최근 작성한 문서를 조회한다.
     *
     * @param writerId 작성자 회원 ID
     * @param limit 조회할 최대 문서 수
     * @return 최근 작성 문서 목록
     */
    List<RecentDocumentResponse> findRecentDocuments(
            Long writerId,
            long limit
    );

    /**
     * 사용자에게 배정된 결재 대기 문서를 조회한다.
     *
     * @param approverId 결재자 회원 ID
     * @param limit 조회할 최대 문서 수
     * @return 결재 대기 문서 목록
     */
    List<PendingApprovalResponse> findPendingApprovals(
            Long approverId,
            long limit
    );
}
