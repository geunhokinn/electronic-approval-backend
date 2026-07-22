package com.comin.eapproval.auditLog.repository;

import com.comin.eapproval.auditLog.dto.response.AuditLogResponse;
import com.comin.eapproval.auditLog.entity.QAuditLog;
import com.comin.eapproval.auditLog.entity.enums.AuditLogAction;
import com.comin.eapproval.auditLog.entity.enums.AuditLogResult;
import com.comin.eapproval.document.entity.QDocument;
import com.comin.eapproval.member.entity.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * QueryDSL을 이용한 감사 로그 동적 조회 기능을 구현하는 Repository.
 */
@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepositoryCustom {

    private static final QAuditLog auditLog = QAuditLog.auditLog;
    private static final QDocument document = QDocument.document;
    private static final QMember actor = new QMember("actor");

    private final JPAQueryFactory queryFactory;

    /**
     * 조건에 맞는 감사 로그 목록을 조회한다.
     *
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @param action 감사 행위
     * @param result 감사 결과
     * @param pageable 페이징 정보
     * @return 조건에 맞는 감사 로그 목록
     */
    @Override
    public Page<AuditLogResponse> searchAuditLogs(
            LocalDate startDate,
            LocalDate endDate,
            AuditLogAction action,
            AuditLogResult result,
            Pageable pageable
    ) {

        List<AuditLogResponse> content = queryFactory
                .select(Projections.constructor(
                        AuditLogResponse.class,
                        auditLog.createdAt,
                        actor.name,
                        document.title,
                        auditLog.action,
                        auditLog.result,
                        auditLog.reason
                ))
                .from(auditLog)
                .leftJoin(actor)
                .on(auditLog.actorId.eq(actor.id))
                .leftJoin(document)
                .on(auditLog.documentId.eq(document.id))
                .where(
                        createdAtGoe(startDate),
                        createdAtLoe(endDate),
                        actionEq(action),
                        resultEq(result)
                )
                .orderBy(auditLog.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(auditLog.count())
                .from(auditLog)
                .leftJoin(actor)
                .on(auditLog.actorId.eq(actor.id))
                .leftJoin(document)
                .on(auditLog.documentId.eq(document.id))
                .where(
                        createdAtGoe(startDate),
                        createdAtLoe(endDate),
                        actionEq(action),
                        resultEq(result)
                )
                .fetchOne();

        return new PageImpl<>(
                content,
                pageable,
                total != null ? total : 0L
        );
    }

    /**
     * private method.
     */

    /**
     * 조회 시작일 검색 조건을 생성한다.
     *
     * @param startDate 조회 시작일
     * @return 조회 시작일 검색 조건
     */
    private BooleanExpression createdAtGoe(LocalDate startDate) {
        if (startDate == null) {
            return null;
        }

        return auditLog.createdAt.goe(startDate.atStartOfDay());
    }

    /**
     * 조회 종료일 검색 조건을 생성한다.
     *
     * @param endDate 조회 종료일
     * @return 조회 종료일 검색 조건
     */
    private BooleanExpression createdAtLoe(LocalDate endDate) {
        if (endDate == null) {
            return null;
        }

        LocalDateTime endOfDay = endDate.atTime(23, 59, 59);

        return auditLog.createdAt.loe(endOfDay);
    }

    /**
     * 감사 행위 검색 조건을 생성한다.
     *
     * @param action 감사 행위
     * @return 감사 행위 검색 조건
     */
    private BooleanExpression actionEq(AuditLogAction action) {
        return action != null
                ? auditLog.action.eq(action)
                : null;
    }

    /**
     * 감사 결과 검색 조건을 생성한다.
     *
     * @param result 감사 결과
     * @return 감사 결과 검색 조건
     */
    private BooleanExpression resultEq(AuditLogResult result) {
        return result != null
                ? auditLog.result.eq(result)
                : null;
    }
}