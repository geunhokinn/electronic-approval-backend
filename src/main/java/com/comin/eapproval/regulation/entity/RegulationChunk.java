package com.comin.eapproval.regulation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 검색과 임베딩을 위해 규정 원문을 나눈 청크 엔티티입니다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegulationChunk {

    /** MariaDB 규정 청크 식별자입니다. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 청크가 속한 규정 원본입니다. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "regulation_document_id", nullable = false)
    private RegulationDocument regulationDocument;

    /** 청크가 속한 조항 번호입니다. */
    @Column(nullable = false, length = 50)
    private String articleNumber;

    /** 조항 제목입니다. */
    @Column(length = 200)
    private String articleTitle;

    /** 청크가 속한 항 번호입니다. */
    @Column(length = 50)
    private String paragraphNumber;

    /** 조항·항에서 추출한 청크 원문입니다. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Qdrant에 저장된 Point 식별자입니다. */
    @Column(length = 100)
    private String vectorId;

    @Builder
    private RegulationChunk(RegulationDocument regulationDocument,
                            String articleNumber, String articleTitle, String paragraphNumber,
                            String content) {
        this.regulationDocument = regulationDocument;
        this.articleNumber = articleNumber;
        this.articleTitle = articleTitle;
        this.paragraphNumber = paragraphNumber;
        this.content = content;
    }

    /**
     * 규정 청크를 생성합니다.
     *
     * @param document 원본 규정
     * @param content 청크 내용
     * @return 생성된 규정 청크
     */
    public static RegulationChunk create(RegulationDocument document,
                                         String articleNumber, String articleTitle,
                                         String paragraphNumber, String content) {
        return RegulationChunk.builder()
                .regulationDocument(document)
                .articleNumber(articleNumber)
                .articleTitle(articleTitle)
                .paragraphNumber(paragraphNumber)
                .content(content.trim())
                .build();
    }

    /**
     * Qdrant Point ID를 저장합니다.
     *
     * @param vectorId Qdrant Point ID
     */
    public void updateVectorId(String vectorId) {
        this.vectorId = vectorId;
    }
}
