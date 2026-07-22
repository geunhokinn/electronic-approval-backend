package com.comin.eapproval.approval.repository;

import com.comin.eapproval.approval.dto.response.ApprovalListResponse;
import com.comin.eapproval.approval.entity.QApproval;
import com.comin.eapproval.document.entity.QDocument;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.entity.QMember;
import com.querydsl.core.types.Projections;
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
 * QueryDSL을 이용한 결재 목록 조회 기능을 구현하는 Repository.
 */
@Repository
@RequiredArgsConstructor
public class ApprovalRepositoryImpl implements ApprovalRepositoryCustom {

    private static final QApproval approval = QApproval.approval;
    private static final QDocument document = QDocument.document;
    private static final QMember writer = new QMember("writer");

    private final JPAQueryFactory queryFactory;

    /**
     * 결재자의 상태별 결재 목록을 조회한다.
     *
     * @param approverId 결재자 회원 ID
     * @param status 문서 상태
     * @param keyword 제목 검색어
     * @param pageable 페이징 정보
     * @return 조건에 맞는 결재 목록
     */
    @Override
    public Page<ApprovalListResponse> findApprovals(
            Long approverId,
            DocumentStatus status,
            String keyword,
            Pageable pageable
    ) {

        List<ApprovalListResponse> content = queryFactory
                .select(Projections.constructor(
                        ApprovalListResponse.class,
                        approval.id,
                        document.id,
                        document.title,
                        writer.name,
                        document.status,
                        approval.createdAt,
                        approval.processedAt
                ))
                .from(approval)
                .join(approval.document, document)
                .join(document.writer, writer)
                .where(
                        approverIdEq(approverId),
                        statusEq(status),
                        titleContains(keyword),
                        notDeleted()
                )
                .orderBy(approval.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(approval.count())
                .from(approval)
                .join(approval.document, document)
                .where(
                        approverIdEq(approverId),
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
     * 문서의 결재자를 조회한다.
     *
     * @param documentId 문서 ID
     * @return 결재자
     */
    @Override
    public Optional<Member> findApproverByDocumentId(
            Long documentId
    ) {
        Member approver = queryFactory
                .select(approval.approver)
                .from(approval)
                .where(
                        approval.document.id.eq(documentId)
                )
                .fetchOne();

        return Optional.ofNullable(approver);
    }

    /**
     * private method.
     */

    /**
     * 결재자 검색 조건을 생성한다.
     *
     * @param approverId 결재자 회원 ID
     * @return 결재자 검색 조건
     */
    private BooleanExpression approverIdEq(Long approverId) {
        return approval.approver.id.eq(approverId);
    }

    /**
     * 문서 상태 검색 조건을 생성한다.
     *
     * @param status 문서 상태
     * @return 문서 상태 검색 조건
     */
    private BooleanExpression statusEq(DocumentStatus status) {
        return document.status.eq(status);
    }

    /**
     * 삭제되지 않은 문서 검색 조건을 생성한다.
     *
     * @return 삭제되지 않은 문서 검색 조건
     */
    private BooleanExpression notDeleted() {
        return document.deletedAt.isNull();
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
}