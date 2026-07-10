package com.comin.eapproval.document.repository;

import com.comin.eapproval.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
