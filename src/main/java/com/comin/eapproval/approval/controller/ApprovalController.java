package com.comin.eapproval.approval.controller;

import com.comin.eapproval.approval.dto.request.RejectApprovalRequest;
import com.comin.eapproval.approval.dto.response.ApprovalListResponse;
import com.comin.eapproval.approval.service.ApprovalService;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.global.response.PageResponse;
import com.comin.eapproval.member.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 결재 관련 API를 제공하는 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    /**
     * 로그인한 결재자의 상태별 결재 목록을 조회한다.
     *
     * @param userDetails 로그인한 회원 정보
     * @param status 조회할 문서 상태
     * @param pageable 페이징 정보
     * @return 상태별 결재 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ApprovalListResponse>>> getApprovals(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam DocumentStatus status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        PageResponse<ApprovalListResponse> approvals =
                approvalService.getApprovals(
                        userDetails.getMemberId(),
                        status,
                        keyword,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(approvals)
        );
    }

    /**
     * 결재를 승인한다.
     *
     * @param approvalId 결재 ID
     * @param userDetails 로그인 사용자 정보
     * @return 결재 승인 결과
     */
    @PatchMapping("/{approvalId}/approve")
    public ResponseEntity<ApiResponse<Void>> approve(
            @PathVariable Long approvalId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        approvalService.approve(
                userDetails.getMemberId(),
                approvalId
        );

        return ResponseEntity.ok(
                ApiResponse.success("결재를 승인했습니다.")
        );
    }

    /**
     * 결재를 반려한다.
     *
     * @param approvalId 결재 ID
     * @param userDetails 로그인 사용자 정보
     * @param request 반려 요청 정보
     * @return 결재 반려 결과
     */
    @PatchMapping("/{approvalId}/reject")
    public ResponseEntity<ApiResponse<Void>> reject(
            @PathVariable Long approvalId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RejectApprovalRequest request
    ) {
        approvalService.reject(
                userDetails.getMemberId(),
                approvalId,
                request
        );

        return ResponseEntity.ok(
                ApiResponse.success("결재를 반려했습니다.")
        );
    }
}