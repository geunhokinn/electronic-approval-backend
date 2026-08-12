package com.comin.eapproval.document.dto.response;

import com.comin.eapproval.document.entity.enums.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * AI Tool이 반환하는 문서 검색 요약 정보입니다.
 */
@Getter
@AllArgsConstructor
public class DocumentToolSearchResponse {

    /** 문서 식별자입니다. */
    private Long documentId;

    /** 문서 제목입니다. */
    private String title;

    /** 문서 상태입니다. */
    private DocumentStatus status;

    /** 결재자 식별자입니다. */
    private Long approverId;

    /** 결재자 이름입니다. */
    private String approverName;

    /** 문서 작성일입니다. */
    private LocalDateTime createdAt;

    /** 문서 수정일입니다. */
    private LocalDateTime updatedAt;

    /**
     * 문서 목록 응답을 Tool 응답으로 변환합니다.
     *
     * @param response 문서 목록 조회 응답
     * @return Tool 문서 검색 응답
     */
    public static DocumentToolSearchResponse from(DocumentListResponse response) {
        return new DocumentToolSearchResponse(
                response.getDocumentId(),
                response.getTitle(),
                response.getStatus(),
                response.getApproverId(),
                response.getApproverName(),
                response.getCreatedAt(),
                response.getUpdatedAt()
        );
    }
}
