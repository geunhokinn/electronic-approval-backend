package com.comin.eapproval.regulation.repository;

import com.comin.eapproval.regulation.entity.RegulationCategory;
import com.comin.eapproval.regulation.entity.RegulationDocument;
import com.comin.eapproval.regulation.entity.RegulationStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.comin.eapproval.regulation.entity.QRegulationDocument.regulationDocument;

/**
 * QueryDSL을 이용한 관리자 규정 동적 조회 기능을 구현하는 Repository입니다.
 */
@Repository
@RequiredArgsConstructor
public class RegulationDocumentRepositoryImpl implements RegulationDocumentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 검색 조건에 맞는 규정 목록을 조회합니다.
     *
     * @param title 규정명 검색어
     * @param category 규정 분류
     * @param status 규정 사용 상태
     * @param pageable 페이지 정보
     * @return 검색 조건에 맞는 규정 목록
     */
    @Override
    public Page<RegulationDocument> searchRegulations(
            String title,
            RegulationCategory category,
            RegulationStatus status,
            Pageable pageable
    ) {
        List<RegulationDocument> content = queryFactory
                .selectFrom(regulationDocument)
                .where(
                        titleContains(title),
                        categoryEq(category),
                        statusEq(status)
                )
                .orderBy(regulationDocument.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(regulationDocument.count())
                .from(regulationDocument)
                .where(
                        titleContains(title),
                        categoryEq(category),
                        statusEq(status)
                )
                .fetchOne();

        return new PageImpl<>(
                content,
                pageable,
                total != null ? total : 0L
        );
    }

    /**
     * 규정명 검색 조건을 생성합니다.
     *
     * @param title 규정명 검색어
     * @return 규정명 검색 조건
     */
    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title)
                ? regulationDocument.title.containsIgnoreCase(title.trim())
                : null;
    }

    /**
     * 규정 분류 검색 조건을 생성합니다.
     *
     * @param category 규정 분류
     * @return 규정 분류 검색 조건
     */
    private BooleanExpression categoryEq(RegulationCategory category) {
        return category != null
                ? regulationDocument.category.eq(category)
                : null;
    }

    /**
     * 규정 사용 상태 검색 조건을 생성합니다.
     *
     * @param status 규정 사용 상태
     * @return 규정 사용 상태 검색 조건
     */
    private BooleanExpression statusEq(RegulationStatus status) {
        return status != null
                ? regulationDocument.status.eq(status)
                : null;
    }
}
