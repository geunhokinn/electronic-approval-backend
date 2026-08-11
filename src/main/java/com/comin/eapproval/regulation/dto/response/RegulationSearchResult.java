package com.comin.eapproval.regulation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Qdrant에서 검색된 규정 청크 결과입니다.
 */
@Getter
@AllArgsConstructor
public class RegulationSearchResult {

    /** Qdrant Point ID입니다. */
    private String pointId;

    /** 질문과 청크의 유사도 점수입니다. */
    private double score;

    /** 규정 ID입니다. */
    private Long regulationId;

    /** 규정명입니다. */
    private String regulationName;

    /** 규정 분류입니다. */
    private String category;

    /** 규정 버전입니다. */
    private String version;

    /** 조항 번호입니다. */
    private String articleNumber;

    /** 조항 제목입니다. */
    private String articleTitle;

    /** 항 번호입니다. */
    private String paragraphNumber;

    /** 검색된 규정 본문입니다. */
    private String content;
}
