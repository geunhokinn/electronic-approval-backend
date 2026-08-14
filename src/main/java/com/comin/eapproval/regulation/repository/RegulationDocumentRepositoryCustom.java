package com.comin.eapproval.regulation.repository;

import com.comin.eapproval.regulation.entity.RegulationCategory;
import com.comin.eapproval.regulation.entity.RegulationDocument;
import com.comin.eapproval.regulation.entity.RegulationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * QueryDSL을 이용한 관리자 규정 동적 조회 기능을 정의하는 Repository입니다.
 */
public interface RegulationDocumentRepositoryCustom {

    /**
     * 검색 조건에 맞는 규정 목록을 조회합니다.
     *
     * @param title 규정명 검색어
     * @param category 규정 분류
     * @param status 규정 사용 상태
     * @param pageable 페이지 정보
     * @return 검색 조건에 맞는 규정 목록
     */
    Page<RegulationDocument> searchRegulations(
            String title,
            RegulationCategory category,
            RegulationStatus status,
            Pageable pageable
    );
}
