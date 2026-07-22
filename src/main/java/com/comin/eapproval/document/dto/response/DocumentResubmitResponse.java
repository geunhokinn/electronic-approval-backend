package com.comin.eapproval.document.dto.response;

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

    @QueryProjection
    public DocumentResubmitResponse(
            String title,
            String content,
            Long approverId,
            String approverName
    ) {
        this.title = title;
        this.content = content;
        this.approverId = approverId;
        this.approverName = approverName;
    }
}