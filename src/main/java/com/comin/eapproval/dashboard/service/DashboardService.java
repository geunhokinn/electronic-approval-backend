package com.comin.eapproval.dashboard.service;

import com.comin.eapproval.dashboard.dto.response.DashboardResponse;
import com.comin.eapproval.dashboard.dto.response.DashboardNoticeResponse;
//import com.comin.eapproval.dashboard.dto.response.DocumentCountResponse;
//import com.comin.eapproval.dashboard.dto.response.PendingApprovalResponse;
import com.comin.eapproval.dashboard.dto.response.RecentDocumentResponse;
import com.comin.eapproval.dashboard.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 대시보드 정보를 조회하는 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    /**
     * 대시보드 목록 조회 개수.
     */
    private static final long DASHBOARD_LIMIT = 3L;

    private final DashboardRepository dashboardRepository;

    /**
     * 로그인 사용자의 대시보드 정보를 조회한다.
     *
     * @param memberId 로그인 사용자 ID
     * @return 대시보드 정보
     */
    public DashboardResponse getDashboard(Long memberId) {

//        DocumentCountResponse documentCounts =
//                dashboardRepository.countDocumentsByStatus(memberId);

        List<DashboardNoticeResponse> notices =
                dashboardRepository.findRecentNotices(DASHBOARD_LIMIT);

        List<RecentDocumentResponse> recentDocuments =
                dashboardRepository.findRecentDocuments(
                        memberId,
                        DASHBOARD_LIMIT
                );

//        List<PendingApprovalResponse> pendingApprovals =
//                dashboardRepository.findPendingApprovals(
//                        memberId,
//                        DASHBOARD_LIMIT
//                );

        return new DashboardResponse(
//                documentCounts,
                notices,
                recentDocuments
//                pendingApprovals
        );
    }
}
