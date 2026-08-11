package com.comin.eapproval.regulation.repository;

import com.comin.eapproval.regulation.entity.RegulationChunk;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 규정 청크 데이터 접근 기능을 제공합니다.
 */
public interface RegulationChunkRepository extends JpaRepository<RegulationChunk, Long> {
}
