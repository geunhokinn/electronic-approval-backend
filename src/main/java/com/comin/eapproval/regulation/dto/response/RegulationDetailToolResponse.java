package com.comin.eapproval.regulation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * AI 규정 조회 Tool이 반환하는 규정 상세 정보입니다.
 */
@Getter
@AllArgsConstructor
public class RegulationDetailToolResponse {

    /** 규정 식별자입니다. */
    private Long regulationId;

    /** 규정명입니다. */
    private String regulationName;

    /** 규정 분류입니다. */
    private String category;

    /** 규정 버전입니다. */
    private String version;

    /** 규정 시행일입니다. */
    private LocalDate effectiveDate;

    /** 조회된 조항 번호입니다. */
    private String articleNumber;

    /** 조회된 조항 제목입니다. */
    private String articleTitle;

    /** 조회된 항 번호입니다. */
    private String paragraphNumber;

    /** 조회된 조항 또는 항의 원문입니다. */
    private String content;
}
