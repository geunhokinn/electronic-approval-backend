package com.comin.eapproval.approval.service;

import com.comin.eapproval.approval.dto.request.ApprovalDetailToolRequest;
import com.comin.eapproval.approval.dto.response.ApprovalDetailResponse;
import com.comin.eapproval.approval.dto.response.ApprovalToolResponse;
import com.comin.eapproval.approval.repository.ApprovalRepository;
import com.comin.eapproval.chat.dto.response.AiToolResponse;
import com.comin.eapproval.global.security.AiAuthorizationService;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * AI 결재 정보 조회 Tool 기능을 제공하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class ApprovalDetailToolService {

    private final ApprovalService approvalService;
    private final AiAuthorizationService authorizationService;
    private final ApprovalRepository approvalRepository;

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

    /**
     * 문서 ID로 사용자가 조회할 수 있는 결재 정보를 조회합니다.
     *
     * @param documentId 문서 식별자
     * @return Tool 결재 조회 결과
     */
    @Tool(description = "문서 ID로 결재자와 결재 상태를 조회합니다. 사용자가 조회할 수 있는 결재만 반환합니다.")
    public AiToolResponse<ApprovalToolResponse> getApproval(Long documentId) {
        if (documentId == null) {
            return AiToolResponse.failure("INVALID_DOCUMENT_ID", "문서 ID가 필요합니다.");
        }

        CustomUserDetails userDetails = currentUser();
        Long approvalId = approvalRepository.findByDocument_Id(documentId)
                .map(approval -> approval.getId())
                .orElse(null);
        if (approvalId == null) {
            return AiToolResponse.failure("APPROVAL_NOT_FOUND", "해당 문서의 결재 정보를 찾을 수 없습니다.");
        }

        ApprovalDetailResponse response = getApprovalDetail(
                new com.comin.eapproval.approval.dto.request.ApprovalDetailToolRequest(approvalId),
                userDetails
        );
        return AiToolResponse.success(ApprovalToolResponse.from(response));
    }

    /** 현재 인증된 사용자를 조회합니다. */
    private CustomUserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            authorizationService.requireAuthenticated(null);
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
