package com.comin.eapproval.regulation.repository;

import com.comin.eapproval.regulation.entity.RegulationChunk;
import com.comin.eapproval.regulation.entity.RegulationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 규정 청크 데이터 접근 기능을 제공합니다.
 */
public interface RegulationChunkRepository extends JpaRepository<RegulationChunk, Long> {

    /** 활성 규정의 특정 조항 청크를 순서대로 조회합니다. */
    List<RegulationChunk> findByRegulationDocument_TitleAndRegulationDocument_StatusAndArticleNumberOrderByChunkIndexAsc(
            String title,
            RegulationStatus status,
            String articleNumber
    );
}
