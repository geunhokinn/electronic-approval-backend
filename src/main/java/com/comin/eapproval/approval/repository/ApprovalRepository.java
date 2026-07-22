package com.comin.eapproval.approval.repository;

import com.comin.eapproval.approval.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval, Long>, ApprovalRepositoryCustom {

    /**
     * 해당 회원이 문서의 결재자인지 조회한다.
     *
     * @param documentId 문서 ID
     * @param approverId 결재자 ID
     * @return 결재자 여부
     */
    boolean existsByDocumentIdAndApproverId(
            Long documentId,
            Long approverId
    );
}
