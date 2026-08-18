package com.comin.eapproval.regulation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

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

    /** 규정 요약에 사용할 원문 일부입니다. */
    private String overviewContent;

    /** 규정에 포함된 조항 요약 목록입니다. */
    private List<RegulationArticleSummary> articles;

    /** 조회된 조항 또는 항의 원문입니다. */
    private List<RegulationSearchToolResponse> contents;

    /**
     * 규정 조항의 요약 정보입니다.
     */
    @Getter
    @AllArgsConstructor
    public static class RegulationArticleSummary {

        /** 조항 번호입니다. */
        private String articleNumber;

        /** 조항 제목입니다. */
        private String articleTitle;
    }
}
