package com.comin.eapproval.regulation.repository;

import com.comin.eapproval.regulation.entity.RegulationChunk;
import com.comin.eapproval.regulation.entity.RegulationStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.comin.eapproval.regulation.entity.QRegulationChunk.regulationChunk;
import static com.comin.eapproval.regulation.entity.QRegulationDocument.regulationDocument;

/**
 * QueryDSL을 이용한 규정 청크 조회 기능을 구현하는 Repository입니다.
 */
@Repository
@RequiredArgsConstructor
public class RegulationChunkRepositoryImpl implements RegulationChunkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /** 활성 규정의 특정 조항 청크를 순서대로 조회합니다. */
    @Override
    public List<RegulationChunk> findArticleChunks(Long regulationId, RegulationStatus status, String articleNumber) {
        return queryFactory
                .selectFrom(regulationChunk)
                .join(regulationChunk.regulationDocument, regulationDocument).fetchJoin()
                .where(
                        regulationDocument.id.eq(regulationId),
                        regulationDocument.status.eq(status),
                        regulationChunk.articleNumber.eq(articleNumber)
                )
                .orderBy(regulationChunk.id.asc())
                .fetch();
    }
}
