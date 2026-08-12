package com.comin.eapproval.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * AI 답변에 사용된 규정 출처를 표현하는 DTO.
 */
@Getter
@AllArgsConstructor
public class RagSource {

    private Long regulationId;
    private String regulationName;
    private String category;
    private String articleNumber;
    private String articleTitle;
    private String paragraphNumber;
    private String version;
    private LocalDate effectiveDate;
    private double score;

    /**
     * RAG 근거를 사용자 표시용 출처로 변환한다.
     *
     * @param evidence Nova에 전달된 규정 근거
     * @return 규정 출처
     */
    public static RagSource from(RagEvidence evidence) {
        return new RagSource(
                evidence.getRegulationId(),
                evidence.getRegulationName(),
                evidence.getCategory(),
                evidence.getArticleNumber(),
                evidence.getArticleTitle(),
                evidence.getParagraphNumber(),
                evidence.getVersion(),
                evidence.getEffectiveDate(),
                evidence.getScore()
        );
    }
}
