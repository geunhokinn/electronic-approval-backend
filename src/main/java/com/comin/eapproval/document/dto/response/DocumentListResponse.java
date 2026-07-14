package com.comin.eapproval.document.dto.response;

import com.comin.eapproval.document.entity.Document;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 내 문서 목록 조회 결과를 제공하는 응답 객체.
 */
@Getter
public class DocumentListResponse {

    private final Long documentId;
    private final String title;
    private final String approverName;
    private final DocumentStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private DocumentListResponse(
            Long documentId,
            String title,
            String approverName,
            DocumentStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.documentId = documentId;
        this.title = title;
        this.approverName = approverName;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Document 엔티티를 문서 목록 응답으로 변환한다.
     *
     * @param document 문서 엔티티
     * @return 문서 목록 응답
     */
    public static DocumentListResponse from(Document document) {
        String approverName = document.getApprover() == null
                ? null
                : document.getApprover().getName();

        return new DocumentListResponse(
                document.getId(),
                document.getTitle(),
                approverName,
                document.getStatus(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}
