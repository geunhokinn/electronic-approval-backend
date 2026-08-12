package com.comin.eapproval.approval.dto.response;

import com.comin.eapproval.document.entity.enums.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * AI Tool이 반환하는 결재 요약 정보입니다.
 */
@Getter
@AllArgsConstructor
public class ApprovalToolResponse {

    /** 결재 식별자입니다. */
    private Long approvalId;

    /** 문서 식별자입니다. */
    private Long documentId;

    /** 문서 제목입니다. */
    private String title;

    /** 문서 본문입니다. 최대 1,000자만 반환합니다. */
    private String content;

    /** 문서 상태입니다. */
    private DocumentStatus status;

    /** 작성자 이름입니다. */
    private String writerName;

    /** 결재자 이름입니다. */
    private String approverName;

    /** 결재 요청일입니다. */
    private LocalDateTime requestedAt;

    /** 결재 처리일입니다. */
    private LocalDateTime processedAt;

    /** 반려 의견입니다. */
    private String comment;

    /**
     * 결재 상세 응답을 Tool 응답으로 변환합니다.
     *
     * @param response 결재 상세 조회 응답
     * @return Tool 결재 응답
     */
    public static ApprovalToolResponse from(ApprovalDetailResponse response) {
        String content = response.getContent();
        if (content != null && content.length() > 1_000) {
            content = content.substring(0, 1_000);
        }
        return new ApprovalToolResponse(
                response.getApprovalId(),
                response.getDocumentId(),
                response.getTitle(),
                content,
                response.getStatus(),
                response.getWriterName(),
                response.getApproverName(),
                response.getRequestedAt(),
                response.getProcessedAt(),
                response.getComment()
        );
    }
}
