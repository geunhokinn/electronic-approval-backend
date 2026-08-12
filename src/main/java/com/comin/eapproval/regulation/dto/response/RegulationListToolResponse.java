package com.comin.eapproval.regulation.dto.response;

import com.comin.eapproval.regulation.entity.RegulationDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * AI Tool이 반환하는 활성 규정 요약 정보입니다.
 */
@Getter
@AllArgsConstructor
public class RegulationListToolResponse {

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

    /**
     * 규정 엔티티를 Tool 응답으로 변환합니다.
     *
     * @param document 규정 원본 엔티티
     * @return 규정 목록 응답
     */
    public static RegulationListToolResponse from(RegulationDocument document) {
        return new RegulationListToolResponse(
                document.getId(),
                document.getTitle(),
                document.getCategory().name(),
                document.getVersion(),
                document.getEffectiveDate()
        );
    }
}
