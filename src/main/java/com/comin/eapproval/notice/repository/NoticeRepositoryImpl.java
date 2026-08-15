package com.comin.eapproval.notice.repository;

import com.comin.eapproval.member.entity.QMember;
import com.comin.eapproval.notice.dto.response.NoticeDetailResponse;
import com.comin.eapproval.notice.dto.response.NoticeListResponse;
import com.comin.eapproval.notice.entity.QNotice;
import com.comin.eapproval.notice.entity.NoticeCategory;
import com.comin.eapproval.notice.entity.NoticeType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
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

/**
 * QueryDSL을 이용한 공지사항 조회 기능을 구현하는 Repository.
 */
@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    private static final QNotice notice = QNotice.notice;
    private static final QMember author = new QMember("author");

    private final JPAQueryFactory queryFactory;

    /**
     * 중요 공지를 우선하여 공지사항 목록을 조회한다.
     *
     * @param keyword 제목 검색어
     * @param noticeType 공지 유형
     * @param noticeCategory 공지 분류
     * @param pageable 페이징 정보
     * @return 공지사항 목록
     */
    @Override
    public Page<NoticeListResponse> searchNotices(
            String keyword,
            NoticeType noticeType,
            NoticeCategory noticeCategory,
            Pageable pageable
    ) {
        List<NoticeListResponse> content = queryFactory
                .select(Projections.constructor(
                        NoticeListResponse.class,
                        notice.id,
                        notice.title,
                        notice.noticeType,
                        notice.noticeCategory,
                        author.name,
                        notice.createdAt
                ))
                .from(notice)
                .join(notice.author, author)
                .where(
                        titleContains(keyword),
                        noticeTypeEq(noticeType),
                        noticeCategoryEq(noticeCategory),
                        notDeleted()
                )
                .orderBy(
                        new CaseBuilder()
                                .when(notice.noticeType.eq(NoticeType.IMPORTANT)).then(0)
                                .otherwise(1).asc(),
                        notice.createdAt.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(notice.count())
                .from(notice)
                .where(
                        titleContains(keyword),
                        noticeTypeEq(noticeType),
                        noticeCategoryEq(noticeCategory),
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
     * 공지사항 ID로 상세 정보를 조회한다.
     *
     * @param noticeId 공지사항 ID
     * @return 공지사항 상세 정보
     */
    @Override
    public Optional<NoticeDetailResponse> findNoticeDetailById(Long noticeId) {
        NoticeDetailResponse result = queryFactory
                .select(Projections.constructor(
                        NoticeDetailResponse.class,
                        notice.id,
                        notice.title,
                        notice.content,
                        notice.noticeType,
                        notice.noticeCategory,
                        author.id,
                        author.name,
                        notice.createdAt,
                        notice.updatedAt
                ))
                .from(notice)
                .join(notice.author, author)
                .where(
                        notice.id.eq(noticeId),
                        notDeleted()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * 공지사항 제목 검색 조건을 생성한다.
     *
     * @param keyword 검색어
     * @return 제목 검색 조건
     */
    private BooleanExpression titleContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        return notice.title.containsIgnoreCase(keyword.trim());
    }

    /**
     * 공지 유형 검색 조건을 생성한다.
     *
     * @param noticeType 공지 유형
     * @return 공지 유형 검색 조건
     */
    private BooleanExpression noticeTypeEq(NoticeType noticeType) {
        return noticeType == null ? null : notice.noticeType.eq(noticeType);
    }

    /**
     * 공지 분류 검색 조건을 생성한다.
     *
     * @param noticeCategory 공지 분류
     * @return 공지 분류 검색 조건
     */
    private BooleanExpression noticeCategoryEq(NoticeCategory noticeCategory) {
        return noticeCategory == null ? null : notice.noticeCategory.eq(noticeCategory);
    }

    /**
     * 삭제되지 않은 공지사항 조회 조건을 생성한다.
     *
     * @return 삭제되지 않은 공지사항 조회 조건
     */
    private BooleanExpression notDeleted() {
        return notice.deletedAt.isNull();
    }
}
