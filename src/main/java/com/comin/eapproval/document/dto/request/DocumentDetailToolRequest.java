package com.comin.eapproval.document.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 문서 상세 조회 Tool 요청 조건을 제공하는 DTO.
 */
@Getter
@AllArgsConstructor
public class DocumentDetailToolRequest {

    @NotNull
    private Long documentId;
}
