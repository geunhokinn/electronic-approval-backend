package com.comin.eapproval.chat.dto.response;

import com.comin.eapproval.regulation.dto.response.RegulationSearchResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Nova에 전달할 규정 근거를 표현하는 DTO.
 */
@Getter
@AllArgsConstructor
public class RagEvidence {

    private double score;
    private Long regulationId;
    private String regulationName;
    private String category;
    private String version;
    private LocalDate effectiveDate;
    private String articleNumber;
    private String articleTitle;
    private String paragraphNumber;
    private String content;

    /** Qdrant 검색 결과를 RAG 근거로 변환한다. */
    public static RagEvidence from(RegulationSearchResult result) {
        return new RagEvidence(
                result.getScore(), result.getRegulationId(), result.getRegulationName(),
                result.getCategory(), result.getVersion(), result.getEffectiveDate(),
                result.getArticleNumber(), result.getArticleTitle(),
                result.getParagraphNumber(), result.getContent()
        );
    }
}
