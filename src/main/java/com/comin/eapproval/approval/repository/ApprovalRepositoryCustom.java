package com.comin.eapproval.approval.repository;

import com.comin.eapproval.approval.dto.response.ApprovalDetailResponse;
import com.comin.eapproval.approval.dto.response.ApprovalListResponse;
import com.comin.eapproval.approval.entity.enums.ApprovalBox;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 결재 QueryDSL 조회 기능을 제공하는 커스텀 Repository
 */
public interface ApprovalRepositoryCustom {

    /**
     * 결재자의 문서를 업무 구분별로 조회한다.
     *
     * @param approverId 결재자 ID
     * @param box 조회할 결재 목록 구분
     * @param status 완료함 상태 필터
     * @param keyword 제목 검색어
     * @param pageable 페이징 정보
     * @return 업무 구분별 결재 목록
     */
    Page<ApprovalListResponse> findApprovals(
            Long approverId,
            ApprovalBox box,
            DocumentStatus status,
            String keyword,
            Pageable pageable
    );

    /**
     * 결재 ID로 삭제되지 않은 문서의 결재 상세 정보를 조회한다.
     *
     * @param approvalId 결재 ID
     * @return 결재 상세 정보
     */
    Optional<ApprovalDetailResponse> findApprovalDetailById(Long approvalId);

    /**
     * 문서 ID로 결재자를 조회한다.
     *
     * @param documentId 문서 ID
     * @return 조회된 결재자
     */
    Optional<Member> findApproverByDocumentId(Long documentId);
}
