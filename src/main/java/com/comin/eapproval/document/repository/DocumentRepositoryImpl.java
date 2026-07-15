package com.comin.eapproval.document.repository;

import com.comin.eapproval.document.entity.Document;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.comin.eapproval.document.entity.QDocument.document;

/**
 * QueryDSL을 이용한 문서 동적 조회 기능을 구현하는 Repository.
 */
@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 로그인 사용자가 작성한 문서 목록을 조회한다.
     *
     * 삭제된 문서는 조회 대상에서 제외한다.
     *
     * @param writerId 작성자 회원 ID
     * @param status 문서 상태
     * @param keyword 제목 검색어
     * @param pageable 페이징 정보
     * @return 조건에 맞는 문서 목록
     */
    @Override
    public Page<Document> searchMyDocuments(
            Long writerId,
            DocumentStatus status,
            String keyword,
            Pageable pageable) {

        List<Document> content = queryFactory
                .selectFrom(document)
                .where(
                        writerIdEq(writerId),
                        statusEq(status),
                        titleContains(keyword),
                        notDeleted()
                )
                .orderBy(document.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(document.count())
                .from(document)
                .where(
                        writerIdEq(writerId),
                        statusEq(status),
                        titleContains(keyword),
                        notDeleted()
                )
                .fetchOne();

        return new PageImpl<>(
                content,
                pageable,
                total != null ? total : 0L
        );
    }

    /**
     * 문서 ID로 삭제되지 않은 문서의 상세 정보를 조회한다.
     *
     * 작성자 정보를 함께 조회한다.
     *
     * @param documentId 문서 ID
     * @return 조회된 문서
     */
    @Override
    public Optional<Document> findDetailById(Long documentId) {
        Document result = queryFactory
                .selectFrom(document)
                .join(document.writer).fetchJoin()
                .where(
                        document.id.eq(documentId),
                        notDeleted()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     *  private method.
     */

    /**
     * 작성자 회원 ID 검색 조건을 생성한다.
     *
     * @param writerId 작성자 회원 ID
     * @return 작성자 검색 조건
     */
    private BooleanExpression writerIdEq(Long writerId) {
        return document.writer.id.eq(writerId);
    }

    /**
     * 문서 상태 검색 조건을 생성한다.
     *
     * @param status 문서 상태
     * @return 문서 상태 검색 조건
     */
    private BooleanExpression statusEq(DocumentStatus status) {
        return status != null
                ? document.status.eq(status)
                : null;
    }

    /**
     * 문서 제목 검색 조건을 생성한다.
     *
     * @param keyword 검색어
     * @return 문서 제목 검색 조건
     */
    private BooleanExpression titleContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        return document.title.containsIgnoreCase(keyword.trim());
    }

    /**
     * 삭제되지 않은 문서 검색 조건을 생성한다.
     *
     * @return 삭제되지 않은 문서 검색 조건
     */
    private BooleanExpression notDeleted() {
        return document.deletedAt.isNull();
    }
}
