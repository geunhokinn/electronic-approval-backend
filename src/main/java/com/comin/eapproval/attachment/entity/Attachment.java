package com.comin.eapproval.attachment.entity;

import com.comin.eapproval.document.entity.Document;
import com.comin.eapproval.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 문서에 첨부된 파일의 메타데이터를 저장하는 엔티티입니다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(nullable = false, length = 255)
    private String originalFilename;

    @Column(nullable = false, unique = true, length = 500)
    private String objectKey;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private long fileSize;

    private LocalDateTime deletedAt;

    @Builder
    private Attachment(
            Document document,
            String originalFilename,
            String objectKey,
            String contentType,
            long fileSize
    ) {
        this.document = document;
        this.originalFilename = originalFilename;
        this.objectKey = objectKey;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    /**
     * 문서에 연결할 첨부파일 메타데이터를 생성합니다.
     *
     * @param document 첨부파일이 속한 문서
     * @param originalFilename 원본 파일명
     * @param objectKey S3 객체 키
     * @param contentType 파일 MIME 타입
     * @param fileSize 파일 크기
     * @return 생성한 첨부파일
     */
    public static Attachment create(
            Document document,
            String originalFilename,
            String objectKey,
            String contentType,
            long fileSize
    ) {
        return Attachment.builder()
                .document(document)
                .originalFilename(originalFilename)
                .objectKey(objectKey)
                .contentType(contentType)
                .fileSize(fileSize)
                .build();
    }

    /**
     * 첨부파일을 논리 삭제 처리합니다.
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
