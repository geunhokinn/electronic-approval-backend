package com.comin.eapproval.approvalHistory.repository;

import com.comin.eapproval.approvalHistory.entity.ApprovalHistory;
import com.comin.eapproval.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

    /**
     * 문서의 결재 이력을 생성 순으로 조회
     */
    List<ApprovalHistory> findByDocumentOrderByCreatedAtAsc(Document document);
}
