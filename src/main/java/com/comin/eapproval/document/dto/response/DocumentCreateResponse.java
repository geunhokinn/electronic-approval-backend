package com.comin.eapproval.document.dto.response;

import lombok.Getter;

/**
 * 문서 임시저장 결과를 제공하는 응답 객체.
 */
@Getter
public class DocumentCreateResponse {

    private final Long documentId;

    private DocumentCreateResponse(Long documentId) {
        this.documentId = documentId;
    }

    /**
     * 문서 ID를 문서 임시저장 응답으로 변환한다.
     *
     * @param documentId 문서 ID
     * @return 문서 임시저장 응답
     */
    public static DocumentCreateResponse of(Long documentId) {
        return new DocumentCreateResponse(documentId);
    }
}
