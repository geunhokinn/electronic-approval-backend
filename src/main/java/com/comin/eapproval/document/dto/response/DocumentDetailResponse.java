package com.comin.eapproval.document.dto.response;

import com.comin.eapproval.document.entity.Document;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 문서 상세 조회 결과를 제공하는 응답 객체.
 */
@Getter
public class DocumentDetailResponse {

    private final Long documentId;
    private final String title;
    private final String content;
    private final DocumentStatus status;
    private final Long writerId;
    private final String writerName;
    private final Long approverId;
    private final String approverName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private DocumentDetailResponse(
            Long documentId,
            String title,
            String content,
            DocumentStatus status,
            Long writerId,
            String writerName,
            Long approverId,
            String approverName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.documentId = documentId;
        this.title = title;
        this.content = content;
        this.status = status;
        this.writerId = writerId;
        this.writerName = writerName;
        this.approverId = approverId;
        this.approverName = approverName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Document 엔티티를 문서 상세 응답으로 변환한다.
     *
     * @param document 문서 엔티티
     * @return 문서 상세 응답
     */
    public static DocumentDetailResponse from(Document document) {
        Long approverId = document.getApprover() == null
                ? null
                : document.getApprover().getId();

        String approverName = document.getApprover() == null
                ? null
                : document.getApprover().getName();

        return new DocumentDetailResponse(
                document.getId(),
                document.getTitle(),
                document.getContent(),
                document.getStatus(),
                document.getWriter().getId(),
                document.getWriter().getName(),
                approverId,
                approverName,
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}
