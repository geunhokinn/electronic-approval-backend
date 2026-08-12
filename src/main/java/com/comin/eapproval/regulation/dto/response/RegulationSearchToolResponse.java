package com.comin.eapproval.regulation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * AI Tool이 반환하는 규정 검색 근거 정보입니다.
 */
@Getter
@AllArgsConstructor
public class RegulationSearchToolResponse {

    /** 규정 식별자입니다. */
    private Long regulationId;

    /** 규정명입니다. */
    private String regulationName;

    /** 규정 버전입니다. */
    private String version;

    /** 규정 시행일입니다. */
    private LocalDate effectiveDate;

    /** 조항 번호입니다. */
    private String articleNumber;

    /** 조항 제목입니다. */
    private String articleTitle;

    /** 항 번호입니다. */
    private String paragraphNumber;

    /** 검색된 규정 본문입니다. */
    private String content;
}
