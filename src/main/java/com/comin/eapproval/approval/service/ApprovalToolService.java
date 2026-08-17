package com.comin.eapproval.approval.service;

import com.comin.eapproval.approval.dto.response.ApprovalListResponse;
import com.comin.eapproval.approval.dto.response.ApprovalDetailResponse;
import com.comin.eapproval.approval.dto.response.ApprovalToolResponse;
import com.comin.eapproval.approval.entity.enums.ApprovalBox;
import com.comin.eapproval.approval.repository.ApprovalRepository;
import com.comin.eapproval.chat.dto.response.AiToolResponse;
import com.comin.eapproval.chat.service.ChatToolContext;
import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.approval.ApprovalErrorCode;
import com.comin.eapproval.member.entity.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 로그인한 사용자의 결재함 조회 Tool을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class ApprovalToolService {

    /** AI Tool이 반환할 결재 목록 최대 개수입니다. */
    private static final int MAX_RESULT_SIZE = 10;

    private final ApprovalRepository approvalRepository;

    /**
     * 현재 사용자에게 배정된 결재 대기 문서를 조회합니다.
     *
     * @param toolContext 서버가 주입한 인증 컨텍스트
     * @return 결재 대기 목록
     */
    @Tool(description = "현재 로그인한 사용자에게 배정된 결재 대기 문서를 조회합니다.")
    public AiToolResponse<List<ApprovalListResponse>> searchPendingApprovals(
            ToolContext toolContext
    ) {
        return AiToolResponse.success(search(toolContext, ApprovalBox.PENDING));
    }

    /**
     * 현재 사용자가 처리한 결재 완료 문서를 조회합니다.
     *
     * @param toolContext 서버가 주입한 인증 컨텍스트
     * @return 결재 완료 목록
     */
    @Tool(description = "현재 로그인한 사용자가 승인 또는 반려 처리한 결재 완료 문서를 조회합니다.")
    public AiToolResponse<List<ApprovalListResponse>> searchCompletedApprovals(
            ToolContext toolContext
    ) {
        return AiToolResponse.success(search(toolContext, ApprovalBox.COMPLETED));
    }

    /**
     * 문서 ID로 사용자가 조회할 수 있는 결재 상세 정보를 조회합니다.
     *
     * @param documentId 문서 식별자
     * @param toolContext 서버가 주입한 인증 컨텍스트
     * @return 결재자와 결재 상태
     */
    @Tool(description = "문서 ID로 결재자와 결재 상태를 조회합니다. 사용자가 조회할 수 있는 결재만 반환합니다.")
    public AiToolResponse<ApprovalToolResponse> getApproval(
            Long documentId,
            ToolContext toolContext
    ) {
        if (documentId == null) {
            return AiToolResponse.failure("INVALID_DOCUMENT_ID", "문서 ID가 필요합니다.");
        }

        Long memberId = ChatToolContext.memberId(toolContext);
        MemberRole role = ChatToolContext.role(toolContext);
        Long approvalId = approvalRepository.findByDocument_Id(documentId)
                .map(approval -> approval.getId())
                .orElse(null);
        if (approvalId == null) {
            return AiToolResponse.failure("APPROVAL_NOT_FOUND", "해당 문서의 결재 정보를 찾을 수 없습니다.");
        }

        ApprovalDetailResponse response = approvalRepository
                .findApprovalDetailById(approvalId)
                .orElseThrow(() -> new BusinessException(ApprovalErrorCode.APPROVAL_NOT_FOUND));

        boolean isApprover = response.getApproverId().equals(memberId);
        boolean isAdmin = role == MemberRole.ROLE_ADMIN;
        if (!isApprover && !isAdmin) {
            throw new BusinessException(ApprovalErrorCode.APPROVAL_ACCESS_DENIED);
        }

        return AiToolResponse.success(ApprovalToolResponse.from(response));
    }

    /**
     * 결재함 유형에 따라 공통 조회 로직을 실행합니다.
     *
     * @param toolContext 서버가 주입한 인증 컨텍스트
     * @param box 조회할 결재함 유형
     * @return 결재 목록
     */
    private List<ApprovalListResponse> search(ToolContext toolContext, ApprovalBox box) {
        Long memberId = ChatToolContext.memberId(toolContext);
        return approvalRepository.findApprovals(
                        memberId,
                        box,
                        null,
                        null,
                        PageRequest.of(0, MAX_RESULT_SIZE)
                )
                .getContent();
    }
}
