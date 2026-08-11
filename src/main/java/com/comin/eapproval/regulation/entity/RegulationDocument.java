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

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, length = 30)
    private String version;

    @Column(length = 255)
    private String originalFilename;

    @Column(length = 500)
    private String objectKey;

    @Column(length = 30)
    private String fileType;

    @Column(length = 100)
    private String contentType;

    private Long fileSize;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RegulationStatus status;

    @Builder
    private RegulationDocument(
            String title,
            String category,
            String version,
            String content,
            RegulationStatus status
    ) {
        this.title = title;
        this.category = category;
        this.version = version;
        this.content = content;
        this.status = status;
    }

    /**
     * 규정 메타데이터를 생성합니다.
     */
    public static RegulationDocument create(
            String title,
            String category,
            String version,
            String content
    ) {
        return RegulationDocument.builder()
                .title(title.trim())
                .category(category.trim())
                .version(version.trim())
                .content(content.trim())
                .status(RegulationStatus.ACTIVE)
                .build();
    }

    /**
     * S3 원본 파일 정보를 포함한 규정 메타데이터를 생성합니다.
     */
    public static RegulationDocument createWithFile(
            String title,
            String category,
            String version,
            String originalFilename,
            String objectKey,
            String fileType,
            String contentType,
            Long fileSize,
            String content
    ) {
        RegulationDocument document = create(title, category, version, content);
        document.originalFilename = originalFilename;
        document.objectKey = objectKey;
        document.fileType = fileType;
        document.contentType = contentType;
        document.fileSize = fileSize;
        return document;
    }
}
