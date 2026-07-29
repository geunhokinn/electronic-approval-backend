package com.comin.eapproval.approval.service;

import com.comin.eapproval.approval.dto.request.RejectApprovalRequest;
import com.comin.eapproval.approval.dto.response.ApprovalDetailResponse;
import com.comin.eapproval.approval.dto.response.ApprovalListResponse;
import com.comin.eapproval.approval.entity.Approval;
import com.comin.eapproval.approval.entity.enums.ApprovalBox;
import com.comin.eapproval.approval.repository.ApprovalRepository;
import com.comin.eapproval.auditLog.annotation.Audit;
import com.comin.eapproval.auditLog.context.AuditContext;
import com.comin.eapproval.auditLog.context.AuditContextHolder;
import com.comin.eapproval.auditLog.entity.enums.AuditLogAction;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.approval.ApprovalErrorCode;
import com.comin.eapproval.global.response.PageResponse;
import com.comin.eapproval.member.entity.enums.MemberRole;
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
     * 로그인한 결재자의 업무 구분별 결재 목록을 조회한다.
     *
     * @param memberId 로그인한 회원 ID
     * @param box 조회할 결재 목록 구분
     * @param status 완료함 상태 필터
     * @param keyword 제목 검색어
     * @param pageable 페이징 정보
     * @return 업무 구분별 결재 목록
     */
    public PageResponse<ApprovalListResponse> getApprovals(
            Long memberId,
            ApprovalBox box,
            DocumentStatus status,
            String keyword,
            Pageable pageable
    ) {

        validateSearchCondition(box, status);
        
        Page<ApprovalListResponse> approvals =
                approvalRepository.findApprovals(
                        memberId,
                        box,
                        status,
                        keyword,
                        pageable
                );

        return PageResponse.from(approvals);
    }

    /**
     * 결재 상세 정보를 조회한다.
     *
     * 해당 결재자와 관리자만 조회할 수 있다.
     *
     * @param memberId 로그인 회원 ID
     * @param role 로그인 회원 역할
     * @param approvalId 결재 ID
     * @return 결재 상세 정보
     */
    public ApprovalDetailResponse getApprovalDetail(
            Long memberId,
            MemberRole role,
            Long approvalId
    ) {
        ApprovalDetailResponse response = approvalRepository
                .findApprovalDetailById(approvalId)
                .orElseThrow(() -> new BusinessException(
                        ApprovalErrorCode.APPROVAL_NOT_FOUND
                ));

        boolean isApprover = response.getApproverId().equals(memberId);
        boolean isAdmin = role == MemberRole.ROLE_ADMIN;

        if (!isApprover && !isAdmin) {
            throw new BusinessException(
                    ApprovalErrorCode.APPROVAL_ACCESS_DENIED
            );
        }

        return response;
    }

    /**
     * 결재를 승인한다.
     *
     * @param memberId 로그인한 회원 ID
     * @param approvalId 결재 ID
     */
    @Audit(action = AuditLogAction.APPROVE)
    @Transactional
    public void approve(
            Long memberId,
            Long approvalId
    ) {
        Approval approval = findApproval(approvalId);

        AuditContextHolder.set(
                AuditContext.of(
                        memberId,
                        approval.getDocument().getId()
                )
        );

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
    @Audit(action = AuditLogAction.REJECT)
    @Transactional
    public void reject(
            Long memberId,
            Long approvalId,
            RejectApprovalRequest request
    ) {
        Approval approval = findApproval(approvalId);

        AuditContextHolder.set(
                AuditContext.of(
                        memberId,
                        approval.getDocument().getId()
                )
        );

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
     * 결재 목록 구분과 상태 필터 조합을 검증한다.
     *
     * @param box 조회할 결재 목록 구분
     * @param status 완료함 상태 필터
     */
    private void validateSearchCondition(
            ApprovalBox box,
            DocumentStatus status
    ) {
        boolean pendingHasStatus = box == ApprovalBox.PENDING
                && status != null;
        boolean completedHasInvalidStatus = box == ApprovalBox.COMPLETED
                && status != null
                && status != DocumentStatus.APPROVED
                && status != DocumentStatus.REJECTED;

        if (pendingHasStatus || completedHasInvalidStatus) {
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
