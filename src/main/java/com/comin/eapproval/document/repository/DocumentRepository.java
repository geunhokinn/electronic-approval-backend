package com.comin.eapproval.document.repository;

import com.comin.eapproval.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 문서 데이터 접근 기능을 제공하는 Repository.
 */
public interface DocumentRepository extends JpaRepository<Document, Long>, DocumentRepositoryCustom {
}
