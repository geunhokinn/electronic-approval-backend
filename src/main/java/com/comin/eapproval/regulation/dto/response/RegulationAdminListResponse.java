package com.comin.eapproval.regulation.dto.response;

import com.comin.eapproval.regulation.entity.RegulationCategory;
import com.comin.eapproval.regulation.entity.RegulationDocument;
import com.comin.eapproval.regulation.entity.RegulationIngestionStatus;
import com.comin.eapproval.regulation.entity.RegulationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 관리자 규정 목록 조회 결과를 제공하는 응답 DTO입니다. */
@Getter
@AllArgsConstructor
public class RegulationAdminListResponse {

    /** 규정 식별자입니다. */
    private Long regulationId;
    /** 규정명입니다. */
    private String title;
    /** 규정 분류입니다. */
    private RegulationCategory category;
    /** 규정 버전입니다. */
    private String version;
    /** 규정 시행일입니다. */
    private LocalDate effectiveDate;
    /** 규정 사용 상태입니다. */
    private RegulationStatus status;
    /** AI 적재 처리 상태입니다. */
    private RegulationIngestionStatus ingestionStatus;
    /** 규정 등록일입니다. */
    private LocalDateTime createdAt;

    /** 규정 엔티티를 관리자 목록 응답으로 변환합니다. */
    public static RegulationAdminListResponse from(RegulationDocument document) {
        return new RegulationAdminListResponse(document.getId(), document.getTitle(),
                document.getCategory(), document.getVersion(), document.getEffectiveDate(),
                document.getStatus(), document.getIngestionStatus(), document.getCreatedAt());
    }
}
