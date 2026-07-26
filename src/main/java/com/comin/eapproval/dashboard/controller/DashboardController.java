package com.comin.eapproval.dashboard.controller;

import com.comin.eapproval.dashboard.dto.response.DashboardResponse;
import com.comin.eapproval.dashboard.service.DashboardService;
import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 대시보드 관련 API를 제공하는 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 로그인 사용자의 대시보드 정보를 조회한다.
     *
     * @param userDetails 로그인 사용자 정보
     * @return 대시보드 정보
     */
    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DashboardResponse dashboard =
                dashboardService.getDashboard(userDetails.getMemberId());

        return ResponseEntity.ok(
                ApiResponse.success(dashboard)
        );
    }
}