package com.comin.eapproval.approval.repository;

import com.comin.eapproval.approval.entity.Approval;
import com.comin.eapproval.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
}
