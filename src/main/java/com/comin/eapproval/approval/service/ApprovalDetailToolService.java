package com.comin.eapproval.approval.service;

import com.comin.eapproval.approval.dto.request.ApprovalDetailToolRequest;
import com.comin.eapproval.approval.dto.response.ApprovalDetailResponse;
import com.comin.eapproval.global.security.AiAuthorizationService;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * AI 결재 정보 조회 Tool 기능을 제공하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class ApprovalDetailToolService {

    private final ApprovalService approvalService;
    private final AiAuthorizationService authorizationService;

    /**
     * 인증된 사용자의 결재 접근 권한을 확인한 뒤 결재 상세 정보를 조회한다.
     *
     * @param request 결재 상세 조회 조건
     * @param userDetails 현재 인증 사용자
     * @return 결재 상세 정보
     */
    public ApprovalDetailResponse getApprovalDetail(
            ApprovalDetailToolRequest request,
            CustomUserDetails userDetails
    ) {
        authorizationService.requireAuthenticated(userDetails);

        return approvalService.getApprovalDetail(
                userDetails.getMemberId(),
                MemberRole.valueOf(userDetails.getRole()),
                request.getApprovalId()
        );
    }
}
