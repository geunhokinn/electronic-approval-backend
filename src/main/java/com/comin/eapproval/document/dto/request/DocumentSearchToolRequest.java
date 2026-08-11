package com.comin.eapproval.document.dto.request;

import com.comin.eapproval.document.entity.enums.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 문서 검색 Tool 요청 조건입니다.
 */
@Getter
@AllArgsConstructor
public class DocumentSearchToolRequest {

    private String keyword;
    private DocumentStatus status;
}
