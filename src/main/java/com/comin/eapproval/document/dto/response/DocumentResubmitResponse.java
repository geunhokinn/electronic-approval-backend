package com.comin.eapproval.document.dto.response;

import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

/**
 * 재상신 화면에 표시할 문서 정보를 반환한다.
 */
@Getter
@Builder
public class DocumentResubmitResponse {

    private String title;
    private String content;
    private final Long approverId;
    private final String approverName;
    private final Long resubmittedDocumentId;
    private final DocumentStatus resubmittedDocumentStatus;

    @QueryProjection
    public DocumentResubmitResponse(
            String title,
            String content,
            Long approverId,
            String approverName,
            Long resubmittedDocumentId,
            DocumentStatus resubmittedDocumentStatus
    ) {
        this.title = title;
        this.content = content;
        this.approverId = approverId;
        this.approverName = approverName;
        this.resubmittedDocumentId = resubmittedDocumentId;
        this.resubmittedDocumentStatus = resubmittedDocumentStatus;
    }
}
