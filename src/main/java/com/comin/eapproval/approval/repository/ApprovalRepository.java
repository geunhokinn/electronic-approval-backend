package com.comin.eapproval.approval.repository;

import com.comin.eapproval.approval.entity.Approval;
import com.comin.eapproval.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    /**
     * 문서의 결재 이력을 생성 순으로 조회
     */
    List<Approval> findByDocumentOrderByCreatedAtAsc(Document document);
}
