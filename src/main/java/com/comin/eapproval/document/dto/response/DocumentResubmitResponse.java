package com.comin.eapproval.document.dto.response;

import com.comin.eapproval.document.entity.Document;
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

    public static DocumentResubmitResponse from(Document document) {
        return DocumentResubmitResponse.builder()
                .title(document.getTitle())
                .content(document.getContent())
                .build();
    }
}