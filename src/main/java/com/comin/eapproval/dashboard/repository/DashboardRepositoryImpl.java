package com.comin.eapproval.dashboard.repository;

import com.comin.eapproval.dashboard.dto.response.DocumentCountResponse;
import com.comin.eapproval.dashboard.dto.response.PendingApprovalResponse;
import com.comin.eapproval.dashboard.dto.response.QPendingApprovalResponse;
import com.comin.eapproval.dashboard.dto.response.QRecentDocumentResponse;
import com.comin.eapproval.dashboard.dto.response.RecentDocumentResponse;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.member.entity.QMember;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.comin.eapproval.approval.entity.QApproval.approval;
import static com.comin.eapproval.document.entity.QDocument.document;

/**
 * 대시보드 화면에 필요한 사용자 정의 조회 기능을 구현하는 Repository.
 */
@RequiredArgsConstructor
public class DashboardRepositoryImpl implements DashboardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 작성자가 작성한 문서를 상태별로 집계한다.
     *
     * @param writerId 작성자 회원 ID
     * @return 문서 상태별 건수
     */
    @Override
    public DocumentCountResponse countDocumentsByStatus(Long writerId) {

        List<Tuple> results = queryFactory
                .select(
                        document.status,
                        document.count()
                )
                .from(document)
                .where(
                        document.writer.id.eq(writerId),
                        document.deletedAt.isNull()
                )
                .groupBy(document.status)
                .fetch();

        Map<DocumentStatus, Long> countByStatus =
                new EnumMap<>(DocumentStatus.class);

        for (Tuple result : results) {
            DocumentStatus status = result.get(document.status);
            Long count = result.get(document.count());

            if (status != null && count != null) {
                countByStatus.put(status, count);
            }
        }

        return new DocumentCountResponse(
                countByStatus.getOrDefault(DocumentStatus.DRAFT, 0L),
                countByStatus.getOrDefault(DocumentStatus.PENDING, 0L),
                countByStatus.getOrDefault(DocumentStatus.APPROVED, 0L),
                countByStatus.getOrDefault(DocumentStatus.REJECTED, 0L)
        );
    }

    /**
     * 작성자가 최근 작성한 문서를 조회한다.
     *
     * 결재 요청 전인 임시 저장 문서는 Approval이 없을 수 있으므로
     * Approval을 LEFT JOIN으로 조회한다.
     *
     * @param writerId 작성자 회원 ID
     * @param limit 조회할 최대 문서 수
     * @return 최근 작성 문서 목록
     */
    @Override
    public List<RecentDocumentResponse> findRecentDocuments(
            Long writerId,
            long limit
    ) {
        QMember approver = new QMember("dashboardApprover");

        return queryFactory
                .select(new QRecentDocumentResponse(
                        document.id,
                        document.title,
                        document.status,
                        approver.name,
                        document.createdAt
                ))
                .from(document)
                .leftJoin(approval)
                .on(approval.document.eq(document))
                .leftJoin(approval.approver, approver)
                .where(
                        document.writer.id.eq(writerId),
                        document.deletedAt.isNull()
                )
                .orderBy(
                        document.createdAt.desc(),
                        document.id.desc()
                )
                .limit(limit)
                .fetch();
    }

    /**
     * 결재자에게 배정된 결재 대기 문서를 조회한다.
     *
     * 처리되지 않은 Approval 중 문서 상태가 PENDING인 문서만 조회한다.
     *
     * @param approverId 결재자 회원 ID
     * @param limit 조회할 최대 문서 수
     * @return 결재 대기 문서 목록
     */
    @Override
    public List<PendingApprovalResponse> findPendingApprovals(
            Long approverId,
            long limit
    ) {
        QMember writer = new QMember("dashboardWriter");

        return queryFactory
                .select(new QPendingApprovalResponse(
                        document.id,
                        document.title,
                        writer.name,
                        document.createdAt
                ))
                .from(approval)
                .join(approval.document, document)
                .join(document.writer, writer)
                .where(
                        approval.approver.id.eq(approverId),
                        approval.processedAt.isNull(),
                        document.status.eq(DocumentStatus.PENDING),
                        document.deletedAt.isNull()
                )
                .orderBy(
                        document.createdAt.desc(),
                        document.id.desc()
                )
                .limit(limit)
                .fetch();
    }
}