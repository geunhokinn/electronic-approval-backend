package com.comin.eapproval.regulation.dto.response;

import com.comin.eapproval.regulation.entity.RegulationCategory;
import com.comin.eapproval.regulation.entity.RegulationDocument;
import com.comin.eapproval.regulation.entity.RegulationIngestionStatus;
import com.comin.eapproval.regulation.entity.RegulationStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 규정 상세 조회 결과를 제공하는 응답 DTO입니다. */
@Getter
public class RegulationDetailResponse {

    /** 규정 식별자입니다. */
    private final Long regulationId;
    /** 규정명입니다. */
    private final String title;
    /** 규정 분류입니다. */
    private final RegulationCategory category;
    /** 규정 버전입니다. */
    private final String version;
    /** 규정 시행일입니다. */
    private final LocalDate effectiveDate;
    /** 규정 사용 상태입니다. */
    private final RegulationStatus status;
    /** 규정 AI 적재 상태입니다. */
    private final RegulationIngestionStatus ingestionStatus;
    /** S3에 저장된 원본 파일명입니다. */
    private final String originalFilename;
    /** 원본 파일 MIME 타입입니다. */
    private final String contentType;
    /** 원본 파일 크기입니다. */
    private final Long fileSize;
    /** 규정 등록일입니다. */
    private final LocalDateTime createdAt;
    /** 규정 수정일입니다. */
    private final LocalDateTime updatedAt;

    /**
     * 규정 엔티티를 상세 조회 응답 DTO로 변환합니다.
     *
     * @param document 규정 엔티티
     */
    private RegulationDetailResponse(RegulationDocument document) {
        this.regulationId = document.getId();
        this.title = document.getTitle();
        this.category = document.getCategory();
        this.version = document.getVersion();
        this.effectiveDate = document.getEffectiveDate();
        this.status = document.getStatus();
        this.ingestionStatus = document.getIngestionStatus();
        this.originalFilename = document.getOriginalFilename();
        this.contentType = document.getContentType();
        this.fileSize = document.getFileSize();
        this.createdAt = document.getCreatedAt();
        this.updatedAt = document.getUpdatedAt();
    }

    /**
     * 규정 엔티티를 상세 조회 응답 DTO로 변환합니다.
     *
     * @param document 규정 엔티티
     * @return 규정 상세 조회 응답 DTO
     */
    public static RegulationDetailResponse from(RegulationDocument document) {
        return new RegulationDetailResponse(document);
    }
}
