package com.comin.eapproval.approval.service;

import com.comin.eapproval.approval.dto.request.RejectApprovalRequest;
import com.comin.eapproval.approval.dto.response.ApprovalListResponse;
import com.comin.eapproval.approval.entity.Approval;
import com.comin.eapproval.approval.repository.ApprovalRepository;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.approval.ApprovalErrorCode;
import com.comin.eapproval.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 결재 관련 비즈니스 로직을 처리하는 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalService {

    private final ApprovalRepository approvalRepository;

    /**
     * 로그인한 결재자의 상태별 결재 목록을 조회한다.
     *
     * @param memberId 로그인한 회원 ID
     * @param status 조회할 문서 상태
     * @param pageable 페이징 정보
     * @return 상태별 결재 목록
     */
    public PageResponse<ApprovalListResponse> getApprovals(
            Long memberId,
            DocumentStatus status,
            Pageable pageable
    ) {

        validateSearchStatus(status);
        
        Page<ApprovalListResponse> approvals =
                approvalRepository.findApprovals(
                        memberId,
                        status,
                        pageable
                );

        return PageResponse.from(approvals);
    }

    /**
     * 결재를 승인한다.
     *
     * @param memberId 로그인한 회원 ID
     * @param approvalId 결재 ID
     */
    @Transactional
    public void approve(
            Long memberId,
            Long approvalId
    ) {
        Approval approval = findApproval(approvalId);

        validateApprover(approval, memberId);
        validateNotProcessed(approval);

        approval.approve();
        approval.getDocument().changeToApproved();
    }

    /**
     * 결재를 반려한다.
     *
     * @param memberId 로그인한 회원 ID
     * @param approvalId 결재 ID
     * @param request 반려 요청 정보
     */
    @Transactional
    public void reject(
            Long memberId,
            Long approvalId,
            RejectApprovalRequest request
    ) {
        Approval approval = findApproval(approvalId);

        validateApprover(approval, memberId);
        validateNotProcessed(approval);

        approval.reject(request.getComment());
        approval.getDocument().changeToRejected();
    }

    /**
     * 결재 정보를 조회한다.
     *
     * @param approvalId 결재 ID
     * @return 결재 정보
     */
    private Approval findApproval(Long approvalId) {
        return approvalRepository.findById(approvalId)
                .orElseThrow(() ->
                        new BusinessException(
                                ApprovalErrorCode.APPROVAL_NOT_FOUND
                        )
                );
    }

    /**
     * 결재 목록 조회 가능 상태인지 검증한다.
     *
     * @param status 조회할 문서 상태
     */
    private void validateSearchStatus(DocumentStatus status) {
        if (status == DocumentStatus.DRAFT) {
            throw new BusinessException(
                    ApprovalErrorCode.INVALID_APPROVAL_STATUS
            );
        }
    }

    /**
     * 로그인한 사용자가 결재자인지 검증한다.
     *
     * @param approval 결재 정보
     * @param memberId 로그인한 회원 ID
     */
    private void validateApprover(
            Approval approval,
            Long memberId
    ) {
        if (!approval.getApprover().getId().equals(memberId)) {
            throw new BusinessException(
                    ApprovalErrorCode.NOT_APPROVER
            );
        }
    }

    /**
     * 이미 처리된 결재인지 검증한다.
     *
     * @param approval 결재 정보
     */
    private void validateNotProcessed(Approval approval) {
        if (approval.getProcessedAt() != null) {
            throw new BusinessException(
                    ApprovalErrorCode.ALREADY_PROCESSED_APPROVAL
            );
        }
    }
}