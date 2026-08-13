package com.comin.eapproval.regulation.entity;

import com.comin.eapproval.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 규정 원본 파일의 메타데이터와 버전 정보를 저장하는 엔티티입니다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegulationDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 규정 제목입니다. */
    @Column(nullable = false, length = 200)
    private String title;

    /** 규정 분류입니다. */
    @Column(nullable = false, length = 100)
    private RegulationCategory category;

    /** 규정 시행일입니다. */
    @Column(nullable = false)
    private LocalDate effectiveDate;

    /** 규정 버전입니다. */
    @Column(nullable = false, length = 30)
    private String version;

    /** 사용자가 업로드한 원본 파일명입니다. */
    @Column(length = 255)
    private String originalFilename;

    /** S3에 저장된 원본 객체 키입니다. */
    @Column(length = 500)
    private String objectKey;

    /** 원본 파일 확장자입니다. */
    @Column(length = 30)
    private String fileType;

    /** 원본 파일의 MIME 타입입니다. */
    @Column(length = 100)
    private String contentType;

    /** 원본 파일 크기입니다. */
    private Long fileSize;

    /** 파일에서 추출한 원문 텍스트입니다. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 규정 사용 상태입니다. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RegulationStatus status;

    /** AI 검색 데이터 적재 처리 상태입니다. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RegulationIngestionStatus ingestionStatus;

    @Builder
    private RegulationDocument(
            String title,
            RegulationCategory category,
            String version,
            LocalDate effectiveDate,
            String content,
            RegulationStatus status,
            RegulationIngestionStatus ingestionStatus
    ) {
        this.title = title;
        this.category = category;
        this.version = version;
        this.effectiveDate = effectiveDate;
        this.content = content;
        this.status = status;
        this.ingestionStatus = ingestionStatus;
    }

    /**
     * 규정 메타데이터를 생성합니다.
     */
    public static RegulationDocument create(
            String title,
            RegulationCategory category,
            String version,
            LocalDate effectiveDate,
            String content
    ) {
        return RegulationDocument.builder()
                .title(title.trim())
                .category(category)
                .version(version.trim())
                .effectiveDate(effectiveDate)
                .content(content.trim())
                .status(RegulationStatus.INACTIVE)
                .ingestionStatus(RegulationIngestionStatus.PENDING)
                .build();
    }

    /**
     * S3 원본 파일 정보를 포함한 규정 메타데이터를 생성합니다.
     */
    public static RegulationDocument createWithFile(
            String title,
            RegulationCategory category,
            String version,
            LocalDate effectiveDate,
            String originalFilename,
            String objectKey,
            String fileType,
            String contentType,
            Long fileSize,
            String content
    ) {
        RegulationDocument document = create(
                title,
                category,
                version,
                effectiveDate,
                content
        );
        document.originalFilename = originalFilename;
        document.objectKey = objectKey;
        document.fileType = fileType;
        document.contentType = contentType;
        document.fileSize = fileSize;
        return document;
    }

    /**
     * S3 업로드가 완료된 파일 메타데이터를 반영합니다.
     */
    public void updateFileMetadata(
            String originalFilename,
            String objectKey,
            String fileType,
            String contentType,
            Long fileSize
    ) {
        this.originalFilename = originalFilename;
        this.objectKey = objectKey;
        this.fileType = fileType;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    /**
     * 규정의 사용 상태를 변경합니다.
     *
     * @param status 변경할 규정 사용 상태
     */
    public void updateStatus(RegulationStatus status) {
        this.status = status;
    }

    /**
     * 규정의 AI 적재 처리 상태를 변경합니다.
     *
     * @param ingestionStatus 변경할 AI 적재 처리 상태
     */
    public void updateIngestionStatus(RegulationIngestionStatus ingestionStatus) {
        this.ingestionStatus = ingestionStatus;
    }

    /**
     * 파일에서 추출한 규정 원문을 저장합니다.
     *
     * @param content 추출된 Markdown 원문
     */
    public void updateContent(String content) {
        this.content = content;
    }
}
