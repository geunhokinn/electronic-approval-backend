package com.comin.eapproval.regulation.repository;

import com.comin.eapproval.regulation.entity.RegulationDocument;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 규정 원본 데이터 접근 기능을 제공합니다.
 */
public interface RegulationDocumentRepository extends JpaRepository<RegulationDocument, Long> {

    boolean existsByTitleAndVersion(String title, String version);
}
