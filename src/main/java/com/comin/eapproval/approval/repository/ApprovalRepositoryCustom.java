package com.comin.eapproval.approval.repository;

import com.comin.eapproval.approval.dto.response.ApprovalListResponse;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 결재 QueryDSL 조회 기능을 제공하는 커스텀 Repository
 */
public interface ApprovalRepositoryCustom {

    /**
     * 결재자의 문서를 상태별로 조회한다.
     *
     * @param approverId 결재자 ID
     * @param status 조회할 문서 상태
     * @param pageable 페이징 정보
     * @return 상태별 결재 목록
     */
    Page<ApprovalListResponse> findApprovals(
            Long approverId,
            DocumentStatus status,
            Pageable pageable
    );
}
