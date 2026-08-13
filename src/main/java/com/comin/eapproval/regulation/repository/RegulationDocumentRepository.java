package com.comin.eapproval.regulation.repository;

import com.comin.eapproval.regulation.entity.RegulationDocument;
import com.comin.eapproval.regulation.entity.RegulationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 규정 원본 데이터 접근 기능을 제공합니다.
 */
public interface RegulationDocumentRepository extends JpaRepository<RegulationDocument, Long> {

    boolean existsByTitleAndVersion(String title, String version);

    /** 같은 규정의 활성 버전을 조회합니다. */
    List<RegulationDocument> findByTitleAndStatus(String title, RegulationStatus status);

    /** 활성 상태인 규정 원본 목록을 조회합니다. */
    List<RegulationDocument> findByStatusOrderByTitleAscVersionDesc(RegulationStatus status);

    /** 관리자 규정 목록을 등록일 내림차순으로 페이지 조회합니다. */
    Page<RegulationDocument> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
