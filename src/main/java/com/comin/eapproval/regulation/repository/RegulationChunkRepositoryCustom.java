package com.comin.eapproval.regulation.repository;

import com.comin.eapproval.regulation.entity.RegulationChunk;
import com.comin.eapproval.regulation.entity.RegulationStatus;

import java.util.List;

/**
 * QueryDSL을 이용한 규정 청크 조회 기능을 정의하는 Repository입니다.
 */
public interface RegulationChunkRepositoryCustom {

    /** 활성 규정의 특정 조항 청크를 순서대로 조회합니다. */
    List<RegulationChunk> findArticleChunks(Long regulationId, RegulationStatus status, String articleNumber);
}
